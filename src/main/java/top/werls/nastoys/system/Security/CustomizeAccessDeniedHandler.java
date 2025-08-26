package top.werls.nastoys.system.Security;


import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import top.werls.nastoys.common.ResultData;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
@Slf4j
@Component
public class CustomizeAccessDeniedHandler implements AccessDeniedHandler {

  private final Gson gson;

  public CustomizeAccessDeniedHandler() {
    this.gson = new Gson();
  }

  /**
   * Handles an access denied failure.
   *
   * @param request               that resulted in an <code>AccessDeniedException</code>
   * @param response              so that the user agent can be advised of the failure
   * @param accessDeniedException that caused the invocation
   * @throws IOException      in the event of an IOException
   * @throws ServletException in the event of a ServletException
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    // 记录访问被拒绝的日志信息
    String requestUri = request.getRequestURI();
    String method = request.getMethod();
    String remoteAddr = getClientIpAddress(request);


    // 设置响应头
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    // 构建响应数据
    ResultData<?> resultData = ResultData.notAuth(accessDeniedException.getMessage());

    // 写入响应
    try (PrintWriter writer = response.getWriter()) {
      String jsonResponse = gson.toJson(resultData);
      writer.write(jsonResponse);
      writer.flush();
    } catch (IOException e) {
      log.error("Failed to write access denied response", e);
      throw e;
    }
  }

  /**
   * 获取客户端真实IP地址
   */
  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
