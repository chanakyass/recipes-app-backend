package spring.io.rest.recipes.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.http.HttpHeaders;
import spring.io.rest.recipes.config.JWTServiceProperties;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.security.PayloadDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class JwtTokenUtilImpl implements JwtTokenUtil {

    private final JWTServiceProperties jwtServiceProperties;
    private final AlgorithmStrategy algorithmStrategy;

    public JwtTokenUtilImpl(JWTServiceProperties jwtServiceProperties) {
        this.jwtServiceProperties = jwtServiceProperties;
        this.algorithmStrategy = new AlgorithmStrategy(jwtServiceProperties, new KeyGenerator());
    }

    @Override
    public void validate(String token) throws ApiAccessException {
        try {
            JWTVerifier verifier = JWT.require(algorithmStrategy.getAlgorithm(jwtServiceProperties.getStrategy()))
                    .withIssuer(jwtServiceProperties.getIssuer())
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
        }
        catch (JWTVerificationException jwtException){
            throw new ApiAccessException("Problem with jwt verification");
        }
    }

    public String generateToken(PayloadDetails payloadDetails) throws ApiAccessException {
        String token = "";
        try {
            token = JWT.create()
                    .withSubject(payloadDetails.getUsername()+":"
                            +payloadDetails.getProfileName())
                    .withIssuer(jwtServiceProperties.getIssuer())
                    .sign(algorithmStrategy.getAlgorithm(jwtServiceProperties.getStrategy()));
        } catch (JWTCreationException | IllegalArgumentException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            exception.printStackTrace();
            throw new ApiAccessException("Issue with generating token");
        }

        return token;

    }

    @Override
    public String getSubjectFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getSubject();
        }
        catch (JWTDecodeException exception) {
            exception.printStackTrace();
            throw new ApiAccessException(exception.getLocalizedMessage());
        }
    }


    @Override
    public String extractTokenAndGetSubject(HttpServletRequest request) throws ApiAccessException {

        String token = Optional.of(extractToken(request)).orElseThrow(()->new ApiAccessException("Empty Token"));
        return getSubjectFromToken(token);
    }

    @Override
    public String extractToken(HttpServletRequest request) {

        // Get authorization header and validate
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (header == null || header.isEmpty() || !header.startsWith("Bearer ")) ?
                null : header.split(" ")[1].trim();

    }
}
