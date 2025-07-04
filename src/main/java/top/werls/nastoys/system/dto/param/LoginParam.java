package top.werls.nastoys.system.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import org.springframework.validation.annotation.Validated;

@Data
@Schema(name = "LoginParam", description = "登录参数")
public class LoginParam implements Serializable {

  @Serial
  private static final long serialVersionUID = -1L;
  @Schema(description = "用户名", requiredMode = RequiredMode.REQUIRED, example = "admin")
  @NotBlank(message = "用户名不能为空")
  private String username;
  @Schema(description = "密码", requiredMode = RequiredMode.REQUIRED, example = "admin")
  @NotBlank(message = "密码不能为空")
  @Size( min = 6, max = 20, message = "密码长度必须在6到20之间")
  private String password;
}

