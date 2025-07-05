package top.werls.nastoys.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 密码修改
 *
 * @author JiaWei Lee
 * @since on 05 7月 2025
 * @version 1
 */
@Getter
@Setter
public class ResetPasswordRequest implements Serializable {

  @NotBlank(message = "密码不能为空")
  private String oldPassword;
  @NotBlank(message = "密码不能为空")
  @Size( min = 6, max = 20, message = "密码长度必须在6到20之间")
  private String newPassword;

}
