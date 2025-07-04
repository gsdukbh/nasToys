package top.werls.nastoys.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.werls.nastoys.system.entity.SysUser;
import top.werls.nastoys.system.repository.SysUserRepository;

/**
 * 初始化数据信息
 *
 * @author JiaWei Lee
 * @since on 04 7月 2025
 * @version 1
 */
@Slf4j
@Component
public class InitDataBase  implements ApplicationRunner {

  private PasswordEncoder passwordEncoder;

  private SysUserRepository sysUserRepository;

  public InitDataBase(PasswordEncoder passwordEncoder, SysUserRepository sysUserRepository) {
    this.passwordEncoder = passwordEncoder;
    this.sysUserRepository = sysUserRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.debug("初始化用户信息 添加默认用户");

    var old =  sysUserRepository.findByUsername("admin");
    if (old != null) {
      log.debug("用户 admin 已存在，跳过初始化");
      return;
    }

    SysUser admin  =new SysUser();
    admin.setUsername("admin");
    admin.setPassword(passwordEncoder.encode("admin"));
    admin.setNickname("管理员");
    admin.setEmail("root@werls.top");
    sysUserRepository.save(admin);


  }

}
