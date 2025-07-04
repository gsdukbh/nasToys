package top.werls.nastoys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 设备
 *
 * @author JiaWei Lee
 * @since on 04 7月 2025
 * @version 1
 */
@Getter
@Setter
@Entity
@Table(name = "machine")
public class Machine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Schema( description = "设备名称", example = "My PC")
  @NotBlank(message = "设备名称不能为空")
  private String name;

  @Schema(description = "设备Mac地址", example = " 00:1A:2B:3C:4D:5E")
  @NotBlank(message = "MAC地址不能为空")
  private String mac;

  @Schema(description = "设备广播地址", example = "192.168.1.255")
  @NotBlank(message = "广播地址不能为空")
  private String broadcastIp;

}
