package top.werls.nastoys.system.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@ToString
@Schema(description = "用户实体类")
@Entity
@Table(
    name = "sys_user",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uc_sysuser_username",
          columnNames = {"username"})
    })
public class SysUser implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uid;

  @NotBlank(message = "用户名不能为空")
  @Schema(description = "用户名", example = "admin")
  private String username;

  @Schema(description = "密码", example = "123456")
  @ToString.Exclude
  @NotBlank(message = "密码不能为空")
  private String password;

  @ToString.Exclude @Schema private String salt;

  @Schema(description = "电话", example = "1231")
  private String phone;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "昵称", example = "admin")
  private String nickname;

  @Schema(description = "是否启用")
  private boolean enabled;

  @Schema(description = "账户未过期")
  private boolean accountNonExpired;

  @Schema(description = "凭据未过期")
  private boolean credentialsNonExpired;

  @Schema(description = "账户未锁定")
  private boolean accountNonLocked;

}
