package top.werls.nastoys;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.werls.nastoys.system.entity.SysUser;

@SpringBootTest
class SpringBootTemplateApplicationTests {

    @Test
    void contextLoads() {
        SysUser user =new SysUser();
        user.setUsername( "test");
    }

}
