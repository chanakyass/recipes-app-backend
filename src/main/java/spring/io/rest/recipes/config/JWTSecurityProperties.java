package spring.io.rest.recipes.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import spring.io.rest.recipes.enums.Strategy;

@ConfigurationProperties("app.security.jwt")
@Setter
@Getter
public class JWTSecurityProperties {

    private String issuer;
    private Strategy strategy;
    private String secretKey;
    private String publicKey;
    private String privateKey;

    @Bean("securityProperties")
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
