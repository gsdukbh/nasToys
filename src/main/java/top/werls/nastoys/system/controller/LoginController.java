package top.werls.nastoys.system.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.werls.nastoys.common.ResultData;
import top.werls.nastoys.common.annotation.RequestLimit;
import top.werls.nastoys.system.dto.ResetPasswordRequest;
import top.werls.nastoys.system.dto.param.LoginParam;
import top.werls.nastoys.system.entity.SysUser;
import top.werls.nastoys.system.service.SysUserService;
import top.werls.nastoys.system.dto.vo.LoginVo;


@Slf4j
//@RestController
@Controller
public class LoginController {



  private final SysUserService userService;

  private final PasswordEncoder passwordEncoder;

  public LoginController(PasswordEncoder passwordEncoder, SysUserService userService) {
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  @PostMapping("/api/login")
  @RequestLimit(frequency = 10)
  @ResponseBody
  public ResultData<LoginVo> login(@RequestBody LoginParam param) {

    return ResultData.success(userService.login(param));
  }

  @PostMapping("/api/changePassword")
  @ResponseBody
  public ResultData<String> changePassword( @Valid @RequestBody ResetPasswordRequest user,@AuthenticationPrincipal UserDetails userDetails) {
    var i = userService.findByUsername(userDetails.getUsername());
    if (i == null) {
      return ResultData.fail("用户不存在");
    }
    if (!passwordEncoder.matches(user.getOldPassword(), i.getPassword())) {
      return ResultData.fail("旧密码错误");
    }
    i.setPassword(user.getNewPassword());


    return  ResultData.success(userService.changePassword(i));
  }


  @GetMapping("/")
  public String loginPage(Model model,@AuthenticationPrincipal UserDetails userDetails) {
    // 向页面传递数据
    model.addAttribute("pageTitle", "我的应用 - 登录");
    model.addAttribute("formTitle", "欢迎回来");
    // 检查用户是否已登录

    if (userDetails != null) {
      // 如果用户已登录，重定向到仪表盘
      return "redirect:/dashboard";
    }

    return "login"; // 返回 templates/login.html
  }

  @GetMapping("/login")
  public String login(Model model,@AuthenticationPrincipal UserDetails userDetails) {
    // 向页面传递数据
    model.addAttribute("pageTitle", "我的应用 - 登录");
    model.addAttribute("formTitle", "欢迎回来");
    // 检查用户是否已登录

    if (userDetails != null) {
      // 如果用户已登录，重定向到仪表盘
      return "redirect:/dashboard";
    }

    return "login"; // 返回 templates/login.html
  }


  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("pageTitle", "仪表盘");
    model.addAttribute("activePage", "dashboard"); // 用于高亮侧边栏菜单
    model.addAttribute("view", "dashboard"); // 告诉 layout.html 要加载哪个片段
    return "layout"; // 总是返回主布局文件
  }

  @GetMapping("/devices")
  public String deviceList(Model model) {
    model.addAttribute("pageTitle", "设备列表");
    model.addAttribute("activePage", "devices");
    model.addAttribute("view", "device-list");
    return "layout";
  }

  @GetMapping("/tokens")
  public String tokensPage(Model model) {
    model.addAttribute("pageTitle", "API Tokens");
    model.addAttribute("activePage", "tokens");
    model.addAttribute("view", "api-tokens");
    return "layout";
  }

  @GetMapping("/reset-password")
  public String resetPasswordPage(Model model) {
    model.addAttribute("pageTitle", "重置密码");
    model.addAttribute("activePage", "reset-password");
    model.addAttribute("view", "reset-password");
    return "layout";
  }

  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }
}
