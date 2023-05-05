package spring.io.rest.recipes.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import spring.io.rest.recipes.config.JWTSecurityProperties;
import spring.io.rest.recipes.config.JWTServiceProperties;
import spring.io.rest.recipes.enums.Strategy;
import spring.io.rest.recipes.exceptions.ApiAccessException;

import java.security.GeneralSecurityException;

@Component
@EnableConfigurationProperties(JWTSecurityProperties.class)
public class AlgorithmStrategy {

    private final JWTServiceProperties jwtServiceProperties;
    private final KeyGenerator keyGenerator;

    @Autowired
    public AlgorithmStrategy(JWTServiceProperties jwtServiceProperties, KeyGenerator keyGenerator) {
        this.jwtServiceProperties = jwtServiceProperties;
        this.keyGenerator = new KeyGenerator();
    }

    public Algorithm getAlgorithm(Strategy strategy) throws ApiAccessException {
        Algorithm algorithm = null;
        try {
            switch (strategy) {
                case SYMMETRIC_ENCRYPTION:
                case AUTO:
                    algorithm = Algorithm.HMAC256(jwtServiceProperties.getSecretKey());
                    break;
                case ASYMMETRIC_ENCRYPTION:
                    algorithm = Algorithm.RSA256(keyGenerator.getPublicKeyFromString(jwtServiceProperties.getPublicKey()),
                            keyGenerator.getPrivateKeyFromString(jwtServiceProperties.getPrivateKey()));
                    break;
            }
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
            throw new ApiAccessException("Problem with encryption");
        }
        return algorithm;
    }

}
