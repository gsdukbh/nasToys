package top.werls.nastoys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于记录关机
 *
 * @author JiaWei Lee
 * @since on 05 7月 2025
 * @version 1
 */
@Getter
@Setter
@Entity
public class WolLog {

  @Id
  @GeneratedValue( strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;
  private String name;

  private String mac;

  private Date createTime;
  private Date completedTime;
  private String ip;
  private String broadcastIp;

  @Schema(description = "关机状态", example = "true")
  private Boolean status;

  @Schema(description = "延迟关机时间，单位秒", example = "60")
  private Integer delayTime;

}
