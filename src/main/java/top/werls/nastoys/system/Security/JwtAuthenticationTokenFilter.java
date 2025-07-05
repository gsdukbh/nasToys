package top.werls.nastoys.system.Security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.werls.nastoys.common.utils.JwtTokenUtils;
import top.werls.nastoys.config.ConfigProperties;
import top.werls.nastoys.system.service.ApiTokenService;


import java.io.IOException;
import java.util.Arrays;


/**
 * @author leee
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private final String tokenPrefix;

  private final JwtTokenUtils tokenUtils;

  private final UserDetailsService userDetailsService;

  private final ApiTokenService apiTokenService;

  public JwtAuthenticationTokenFilter(ConfigProperties configProperties, JwtTokenUtils tokenUtils,
      UserDetailsService userDetailsService, ApiTokenService apiTokenService) {
    this.tokenPrefix = configProperties.getJwt().getTokenPrefix();
    this.tokenUtils = tokenUtils;
    this.userDetailsService = userDetailsService;
    this.apiTokenService = apiTokenService;
  }

  /**
   * Same contract as for {@code doFilter}, but guaranteed to be just invoked once per request
   * within a single request thread. See {@link #shouldNotFilterAsyncDispatch()} for details.
   * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
   * default ServletRequest and ServletResponse ones.
   *
   * @param request
   * @param response
   * @param filterChain
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authToken = null;
    if (request.getCookies() != null) {
      authToken = Arrays.stream(request.getCookies())
          .filter(cookie -> "token".equals(cookie.getName()))
          .map(Cookie::getValue)
          .findFirst()
          .orElse(null);
    }

    if (authToken == null) {
      String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
        authToken = authHeader.substring(tokenPrefix.length()).trim();
      }
    }


    if (authToken != null) {
      String username = null;
      try {
        username = tokenUtils.getUsernameFromToken(authToken);
      } catch (Exception e) {
        log.warn("Invalid JWT token: {}", e.getMessage());
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        var apiToken = apiTokenService.findByToken(authToken);
        if (apiToken.isPresent() && !apiToken.get().isRevoked()) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("Authenticated user: {} with API token", username);
        } else if (tokenUtils.validateToken(authToken, userDetails.getUsername())) {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                  userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("Authenticated user: {}", username);
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
