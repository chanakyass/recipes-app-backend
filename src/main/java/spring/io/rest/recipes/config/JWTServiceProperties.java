package spring.io.rest.recipes.config;

import lombok.Getter;
import lombok.Setter;
import spring.io.rest.recipes.enums.Strategy;

@Setter
@Getter
public class JWTServiceProperties {
    private String issuer;
    private Strategy strategy;
    private String secretKey;
    private String publicKey;
    private String privateKey;
}

