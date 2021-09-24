package spring.io.rest.recipes.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import spring.io.rest.recipes.config.JWTSecurityProperties;
import spring.io.rest.recipes.enums.Strategy;
import spring.io.rest.recipes.exceptions.ApiAccessException;

import java.security.GeneralSecurityException;

@Component
@EnableConfigurationProperties(JWTSecurityProperties.class)
public class AlgorithmStrategy {

    private final JWTSecurityProperties JWTSecurityProperties;
    private final KeyGenerator keyGenerator;

    @Autowired
    public AlgorithmStrategy(JWTSecurityProperties JWTSecurityProperties, KeyGenerator keyGenerator) {
        this.JWTSecurityProperties = JWTSecurityProperties;
        this.keyGenerator = keyGenerator;
    }

    public Algorithm getAlgorithm(Strategy strategy) throws ApiAccessException {
        Algorithm algorithm = null;
        try {
            switch (strategy) {
                case SYMMETRIC_ENCRYPTION:
                case AUTO:
                    algorithm = Algorithm.HMAC256(JWTSecurityProperties.getSecretKey());
                    break;
                case ASYMMETRIC_ENCRYPTION:
                    algorithm = Algorithm.RSA256(keyGenerator.getPublicKeyFromString(JWTSecurityProperties.getPublicKey()),
                            keyGenerator.getPrivateKeyFromString(JWTSecurityProperties.getPrivateKey()));
                    break;
            }
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
            throw new ApiAccessException("Problem with encryption");
        }
        return algorithm;
    }

}
