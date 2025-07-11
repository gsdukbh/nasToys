package top.werls.nastoys.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Jiawei Lee
 * @version TODO
 * @date created 2022/7/20
 * @since on
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "env")
public class ConfigProperties {
  /** app mingc */
  private String appName = "template";
  private boolean isEnableSwagger = false;
  private FileProperties fileConfig = new FileProperties();
  private JwtProperties jwt = new JwtProperties();
  private MqttProperties mqtt = new MqttProperties();

  @Data
  public static  class  MqttProperties {
    private String host="ssl://localhost:1883";
    private String clientId="spring-boot-mqtt";
    private String[] defaultTopic={"test"};
    private Integer qos=2;
    private String username="admin";
    private String password="admin";
  }

  @Data
  public static class FileProperties {
    private StorageType type = StorageType.LOCAL;
    private String path = "/upload";
  }

  @Data
  public static class JwtProperties {
    private Integer expire = 30;
    private String tokenHeader = "Authorization";
    private String tokenPrefix = "Bearer ";
    @NotBlank
    private RSAPrivateKey privateKey;
    @NotBlank private RSAPublicKey publicKey;
  }
}
