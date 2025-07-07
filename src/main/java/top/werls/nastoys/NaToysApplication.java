package top.werls.nastoys;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;

/**
 * @author leejiawei
 */
@SpringBootApplication
@Slf4j
public class NaToysApplication {

  public static void main(String[] args) {
    var context = SpringApplication.run(NaToysApplication.class, args);
    var env = context.getEnvironment();
    var port = env.getProperty("server.port");
    BuildProperties buildProperties = context.getBean(BuildProperties.class);
    log.info(
        """
        ==============================================
        = App name : {}
        = App version : {}
        = App build time : {}
        = App run success !!
        = App web url : http://localhost:{}
        = Swagger doc : http://localhost:{}/swagger-ui.html
        ==============================================
        """,
        buildProperties.getName(),
        buildProperties.getVersion(),
        buildProperties.getTime(),
        port,
        port);
  }
}
