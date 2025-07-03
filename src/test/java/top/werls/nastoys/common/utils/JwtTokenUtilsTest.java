package top.werls.nastoys.common.utils;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Jiawei Lee
 * @since on   2024/5/30
 */
@SpringBootTest
class JwtTokenUtilsTest {

  @Resource
  JwtTokenUtils tokenUtils;

  @Test
  void generateToken() {

    String token = tokenUtils.generateTokenWithoutExpiry("test");
    System.out.println(token);
  }
}