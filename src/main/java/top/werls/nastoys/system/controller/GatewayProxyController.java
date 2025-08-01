package top.werls.nastoys.system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author JiaWei Lee
 * @since on 30 7月 2025
 * @version
 */

@Slf4j
@RestController
public class GatewayProxyController {

  // 【核心】这是我们将在客户端注入的拦截脚本
  private static final String INTERCEPTOR_SCRIPT = """
      (function() {
          'use strict';
          const PROXY_PREFIX = '/proxy?url=';
          const originalFetch = window.fetch;
          const originalXhrOpen = window.XMLHttpRequest.prototype.open;
          
          function getAbsoluteUrl(url) {
              return new URL(url, window.location.href).toString();
          }
          
          function rewriteUrl(originalUrl) {
              if (typeof originalUrl !== 'string' || originalUrl.startsWith(PROXY_PREFIX) || originalUrl.startsWith('data:')) {
                  return originalUrl;
              }
              return PROXY_PREFIX + getAbsoluteUrl(originalUrl);
          }
          
          window.fetch = function(input, init) {
              if (input instanceof Request) {
                  const newUrl = rewriteUrl(input.url);
                  const newRequest = new Request(newUrl, {
                      ...input,
                      headers: input.headers,
                      referrer: document.location.href
                  });
                  return originalFetch.call(this, newRequest, init);
              } else {
                  const newUrl = rewriteUrl(input);
                  return originalFetch.call(this, newUrl, init);
              }
          };
          
          window.XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
              const newUrl = rewriteUrl(url);
              return originalXhrOpen.call(this, method, newUrl, async, user, password);
          };
      })();
      """;

  @RequestMapping("/proxy")
  public void proxyRequest(@RequestParam String url, HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) byte[] requestBody) {
    log.info("接收到代理请求. 目标 URL: {}", url);
    HttpURLConnection proxyRequest = null;
    try {
      URL targetUrl = new URI(url).toURL();
      proxyRequest = (HttpURLConnection) targetUrl.openConnection();
      proxyRequest.setRequestMethod(request.getMethod());

      copyRequestHeaders(request, proxyRequest);
      proxyRequest.setRequestProperty(HttpHeaders.HOST, targetUrl.getHost());

      if (requestBody != null && requestBody.length > 0) {
        proxyRequest.setDoOutput(true);
        proxyRequest.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(requestBody.length));
        StreamUtils.copy(requestBody, proxyRequest.getOutputStream());
      }

      int responseCode = proxyRequest.getResponseCode();
      response.setStatus(responseCode);

      copyResponseHeaders(proxyRequest, response);

      String contentType = proxyRequest.getContentType() != null ? proxyRequest.getContentType().toLowerCase() : "";
      InputStream responseStream = getResponseStream(proxyRequest);

      if (contentType.contains("text/html") && responseStream != null) {
        String body = StreamUtils.copyToString(responseStream, StandardCharsets.UTF_8);
        String rewrittenHtml = rewriteHtmlAndInjectScript(body, url);
        byte[] finalBody = rewrittenHtml.getBytes(StandardCharsets.UTF_8);
        response.setContentLength(finalBody.length);
        response.getOutputStream().write(finalBody);
      } else if (responseStream != null) {
        StreamUtils.copy(responseStream, response.getOutputStream());
      }

    } catch (IOException | URISyntaxException e) {
      log.error("代理请求处理失败: {}", e.getMessage(), e);
      handleException(response, e);
    } finally {
      if (proxyRequest != null) {
        proxyRequest.disconnect();
      }
    }
  }

  private String rewriteHtmlAndInjectScript(String html, String baseUri) {
    log.info("开始重写 HTML 并注入拦截脚本 for {}", baseUri);
    Document doc = Jsoup.parse(html, baseUri);

    // 【核心】在 <head> 顶部注入我们的拦截脚本
    doc.head().prepend("<script>" + INTERCEPTOR_SCRIPT + "</script>");

    // 我们仍然需要重写基本的 HTML 属性，以处理非 JS 加载的资源和链接
    doc.select("[href]").forEach(el -> rewriteAttribute(el, "href"));
    doc.select("[src]").forEach(el -> rewriteAttribute(el, "src"));
    doc.select("form[action]").forEach(el -> rewriteAttribute(el, "action"));

    return doc.html();
  }

  private void rewriteAttribute(Element el, String attributeKey) {
    final String originalUrl = el.attr(attributeKey);
    if (shouldSkipRewrite(originalUrl)) return;

    try {
      URL baseUrl = new URI(el.baseUri()).toURL();
      if (isRewritableUrl(originalUrl, baseUrl)) {
        String absoluteUrl = el.absUrl(attributeKey);
        if (!absoluteUrl.isEmpty()) {
          el.attr(attributeKey, "/proxy?url=" + absoluteUrl);
        }
      }
    } catch (Exception e) {
      log.warn("无法解析属性中的URL '{}'。已跳过。", originalUrl);
    }
  }

  private boolean isRewritableUrl(String url, URL baseUrl) throws MalformedURLException, URISyntaxException {
    if (url.startsWith("/")) return true;
    if (url.startsWith("http")) {
      URL targetUrl = new URI(url).toURL();
      return baseUrl.getHost().equalsIgnoreCase(targetUrl.getHost());
    }
    return false;
  }

  private boolean shouldSkipRewrite(String url) {
    return url.isEmpty() || url.contains("/proxy?url=") || url.startsWith("#") || url.toLowerCase().startsWith("javascript:") || url.toLowerCase().startsWith("data:");
  }

  private void copyRequestHeaders(HttpServletRequest from, HttpURLConnection to) { Collections.list(from.getHeaderNames()).forEach(headerName -> { if (!headerName.equalsIgnoreCase(HttpHeaders.HOST) && !headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) && !headerName.equalsIgnoreCase(HttpHeaders.ACCEPT_ENCODING)) { Collections.list(from.getHeaders(headerName)).forEach(headerValue -> to.addRequestProperty(headerName, headerValue)); } }); }
  private void copyResponseHeaders(HttpURLConnection from, HttpServletResponse to) { for (Map.Entry<String, List<String>> header : from.getHeaderFields().entrySet()) { if (header.getKey() != null && !header.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING) && !header.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) { for (String value : header.getValue()) { to.addHeader(header.getKey(), value); } } } }
  private InputStream getResponseStream(HttpURLConnection connection) throws IOException { try { return connection.getInputStream(); } catch (IOException e) { return connection.getErrorStream(); } }
  private void handleException(HttpServletResponse response, Exception e) { try { response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); response.getWriter().write("Proxy failed: " + e.getMessage()); } catch (IOException ex) { log.error("向客户端写入错误信息时发生异常", ex); } }
}