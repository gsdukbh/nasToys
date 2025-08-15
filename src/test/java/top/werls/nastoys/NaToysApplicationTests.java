package top.werls.nastoys;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.werls.nastoys.system.entity.SysUser;

@SpringBootTest
class NaToysApplicationTests {

    @Test
    void contextLoads() {
        SysUser user =new SysUser();
        user.setUsername( "test");
     var a = UUID.randomUUID().toString();
     var up =  new BCryptPasswordEncoder().encode("iloisl");
    System.out.println(up);
    System.out.println(a);
    }

}
