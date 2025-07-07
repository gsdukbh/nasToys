package top.werls.nastoys.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {


  private final BuildProperties buildProperties;

  public SwaggerConfig(BuildProperties buildProperties) {
    this.buildProperties = buildProperties;
  }

  public static final String TOKEN_HEADER = "Authorization";

  @Bean
  public OpenAPI springShopOpenAPI() {
    return new OpenAPI()
        .components(components())
        .info(new Info()
            .title(buildProperties.getName()+"api")
            .description("api msg")
            .version(buildProperties.getVersion()));
  }

  private Components components() {
    Components components = new Components();
    components.addSecuritySchemes(TOKEN_HEADER,
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"));
    return components;
  }

}
