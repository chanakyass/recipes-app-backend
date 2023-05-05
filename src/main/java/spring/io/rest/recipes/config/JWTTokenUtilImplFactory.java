package spring.io.rest.recipes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.io.rest.recipes.security.jwt.JwtTokenUtilImpl;

@Configuration
public class JWTTokenUtilImplFactory {
    private final JWTSecurityProperties jwtSecurityProperties;

    @Autowired
    public JWTTokenUtilImplFactory(JWTSecurityProperties jwtSecurityProperties) {
        this.jwtSecurityProperties = jwtSecurityProperties;
    }

    @Bean("accessTokenUtilImpl")
    public JwtTokenUtilImpl jwtAccessTokenUtilImpl() {
        return new JwtTokenUtilImpl(jwtSecurityProperties.accessTokenProps());
    }

    @Bean("refreshTokenUtilImpl")
    public JwtTokenUtilImpl jwtRefreshTokenUtilImpl() {
        return new JwtTokenUtilImpl(jwtSecurityProperties.refreshTokenProps());
    }
}
