package top.werls.nastoys.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author JiaWei Lee
 * @since on 15 8月 2025
 * @version
 */
class ApiTokenServiceImplTest {

  @Test
  void createToken() {

    var a = UUID.randomUUID().toString();
    var up = new BCryptPasswordEncoder().encode("iloisl");
    System.out.println(up);
    System.out.println(a);
    String s  = Base64.getUrlEncoder().withoutPadding().encodeToString(a.getBytes(StandardCharsets.UTF_8));
    System.out.println(s);
  }
}
