package top.werls.nastoys.system.service.impl;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.werls.nastoys.common.utils.JwtTokenUtils;
import top.werls.nastoys.system.dto.param.LoginParam;
import top.werls.nastoys.system.entity.SysUser;
import top.werls.nastoys.system.repository.SysUserRepository;
import top.werls.nastoys.system.service.SysUserService;
import top.werls.nastoys.system.dto.vo.LoginVo;


@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

  private final SysUserRepository sysUserRepository;


  private final UserDetailsServiceImpl userDetailsService;


  private final PasswordEncoder passwordEncoder;

  private final JwtTokenUtils tokenUtils;

  public SysUserServiceImpl(SysUserRepository sysUserRepository,
      UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder,
      JwtTokenUtils tokenUtils) {
    this.sysUserRepository = sysUserRepository;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
    this.tokenUtils = tokenUtils;
  }

  /**
   * 登录
   *
   * @param param
   * @return
   */
  @Override
  public LoginVo login(LoginParam param) {

    UserDetails userDetails = userDetailsService.loadUserByUsername(param.getUsername());

    if (!passwordEncoder.matches(param.getPassword(), userDetails.getPassword())) {
      throw new BadCredentialsException("密码错误");
    }
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    LoginVo loginVo = new LoginVo();
    loginVo.setToken(tokenUtils.generateToken(userDetails.getUsername()));
    return loginVo;
  }

  @Override
  public String changePassword(SysUser user) {
     var i =  sysUserRepository.findByUsername(user.getUsername());
     if (i== null){
       return "用户不存在";
     }
    if (user.getPassword() == null || user.getPassword().isEmpty()) {
      return "密码不能为空";
    }
    i.setPassword( passwordEncoder.encode(user.getPassword()));

    int o=  sysUserRepository.updatePasswordByUid(i.getPassword(), i.getUid());
    return "ok";
  }

  @Override
  public SysUser findByUsername(String username) {
    return sysUserRepository.findByUsername(username);
  }

  @Override
  public Optional<SysUser> findById(Long id) {
    return sysUserRepository.findById(id);
  }
}
