package top.werls.nastoys.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.werls.nastoys.common.annotation.RequestLimit;
import top.werls.nastoys.common.utils.cache.Cache;
import top.werls.nastoys.common.utils.cache.impl.SimpleCache;

import java.lang.reflect.Method;

/**
 * @author Li JiaWei
 * @version 1
 * @date 2022/11/27
 * @since on
 */
@Aspect
@Component
@Slf4j
public class RequestLimitAspect {


  private final HttpServletRequest request;

  public RequestLimitAspect(HttpServletRequest request) {
    this.request = request;
  }

  private static final Cache<Object, Integer> cache = new SimpleCache<>(1000, 60 * 1000);

  @Pointcut(value = "@annotation(top.werls.nastoys.common.annotation.RequestLimit)")
  public void point() {
  }

  @Around("point()")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    RequestLimit requestLimit = method.getAnnotation(RequestLimit.class);
    int frequency = requestLimit.frequency();
    int minute = requestLimit.minute();
    String key = request.getRemoteAddr();
    if (key == null) {
      throw new RuntimeException("访问过于频繁，请稍后再尝试");
    }
    Integer current = cache.get(key);
    if (current != null) {
      int limit = current + 1;
      cache.replace(key, limit, (long) minute * 60 * 1000);
      if (limit <= frequency) {
        return joinPoint.proceed();
      } else {
        throw new RuntimeException("访问过于频繁，请稍后再尝试");
      }
    } else {
      cache.put(key, 1, (long) minute * 60 * 1000);
      return joinPoint.proceed();
    }
  }
}
