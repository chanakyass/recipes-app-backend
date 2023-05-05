package spring.io.rest.recipes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class JWTSecurityProperties {
    @Bean("accessTokenProps")
    @ConfigurationProperties("app.security.jwt.access")
    public JWTServiceProperties accessTokenProps() {
        return new JWTServiceProperties();
    }

    @Bean("refreshTokenProps")
    @ConfigurationProperties("app.security.jwt.refresh")
    public JWTServiceProperties refreshTokenProps() {
        return new JWTServiceProperties();
    }

    public JWTServiceProperties getTokenPropsFor(String tokenType) {
        if (tokenType.equals("ACCESS_TOKEN")) {
            return accessTokenProps();
        }
        return refreshTokenProps();
    }
}
