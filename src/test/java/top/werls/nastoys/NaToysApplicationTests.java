package top.werls.nastoys;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.werls.nastoys.system.entity.SysUser;

@SpringBootTest
class NaToysApplicationTests {

  @Test
  void contextLoads() {
    SysUser user = new SysUser();
    user.setUsername("test");
  }
}
