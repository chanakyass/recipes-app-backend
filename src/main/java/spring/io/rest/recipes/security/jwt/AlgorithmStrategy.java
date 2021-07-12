package spring.io.rest.recipes.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import spring.io.rest.recipes.config.SecurityProperties;
import spring.io.rest.recipes.enums.Strategy;
import spring.io.rest.recipes.exceptions.ApiAccessException;

import java.security.GeneralSecurityException;

@Component
@EnableConfigurationProperties(SecurityProperties.class)
public class AlgorithmStrategy {

    private final SecurityProperties securityProperties;
    private final KeyGenerator keyGenerator;

    @Autowired
    public AlgorithmStrategy(SecurityProperties securityProperties, KeyGenerator keyGenerator) {
        this.securityProperties = securityProperties;
        this.keyGenerator = keyGenerator;
    }

    public Algorithm getAlgorithm(Strategy strategy) throws ApiAccessException {
        Algorithm algorithm = null;
        try {
            switch (strategy) {
                case SYMMETRIC_ENCRYPTION:
                case AUTO:
                    algorithm = Algorithm.HMAC256(securityProperties.getSecretKey());
                    break;
                case ASYMMETRIC_ENCRYPTION:
                    algorithm = Algorithm.RSA256(keyGenerator.getPublicKeyFromString(securityProperties.getPublicKey()),
                            keyGenerator.getPrivateKeyFromString(securityProperties.getPrivateKey()));
                    break;
            }
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
            throw new ApiAccessException("Problem with encryption");
        }
        return algorithm;
    }

}
