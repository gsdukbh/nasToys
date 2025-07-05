package top.werls.nastoys.system.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.werls.nastoys.common.ResultData;
import top.werls.nastoys.system.entity.ApiToken;
import top.werls.nastoys.system.service.ApiTokenService;
import top.werls.nastoys.system.service.SysUserService;

/**
 * api
 *
 * @author JiaWei Lee
 * @since on 05 7月 2025
 * @version 1
 */
@RestController
@RequestMapping("api/tokens")
public class ApiTokenController {

  private final ApiTokenService apiTokenService;

  private final SysUserService userService;

  public ApiTokenController(ApiTokenService apiTokenService, SysUserService userService) {
    this.apiTokenService = apiTokenService;
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResultData<String> createToken(
      @RequestBody String name, @AuthenticationPrincipal UserDetails userDetails) {
    var i = userService.findByUsername(userDetails.getUsername());
    var res = apiTokenService.createToken(i, name);

    return ResultData.success("", res.getToken());
  }

  @GetMapping("/list")
  public ResultData<List<ApiToken>> list(@AuthenticationPrincipal UserDetails userDetails) {
    var i = userService.findByUsername(userDetails.getUsername());
    List<ApiToken> tokens = apiTokenService.getTokensForUser(i.getUid());
    return ResultData.success(tokens);
  }

  @DeleteMapping("/revoke/{tokenId}")
  public ResultData<String> revokeToken(@PathVariable Long tokenId) {
    apiTokenService.revokeToken(tokenId);
    return ResultData.success("Token revoked successfully");
  }
}
