package top.werls.nastoys.system.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.werls.nastoys.common.ResultData;
import top.werls.nastoys.common.annotation.RequestLimit;
import top.werls.nastoys.system.dto.param.LoginParam;
import top.werls.nastoys.system.service.SysUserService;
import top.werls.nastoys.system.dto.vo.LoginVo;


@Slf4j
@RestController
public class LoginController {


  @Resource
  private SysUserService userService;


  public LoginController(SysUserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  @RequestLimit(frequency = 2)
  public ResultData<LoginVo> login(@RequestBody LoginParam param,
      HttpServletRequest servletRequest) {

    return ResultData.success(userService.login(param));
  }
}
