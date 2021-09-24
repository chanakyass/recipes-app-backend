package spring.io.rest.recipes.unittests.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import spring.io.rest.recipes.config.JWTSecurityProperties;
import spring.io.rest.recipes.enums.Strategy;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.security.PayloadDetails;
import spring.io.rest.recipes.security.UserPrincipal;
import spring.io.rest.recipes.security.jwt.AlgorithmStrategy;
import spring.io.rest.recipes.security.jwt.JwtTokenUtilImpl;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilImplTest {
    @Mock
    private AlgorithmStrategy algorithmStrategy;
    @Mock
    private JWTSecurityProperties JWTSecurityProperties;

    @InjectMocks
    private JwtTokenUtilImpl jwtTokenUtil;

    @BeforeEach
    void setUp() {

    }

    @Test
    void validateFail() {
        Algorithm algorithm = Mockito.mock(Algorithm.class);
        Strategy strategy = Strategy.AUTO;
        when(JWTSecurityProperties.getStrategy()).thenReturn(strategy);
        when(algorithmStrategy.getAlgorithm(any(Strategy.class))).thenReturn(algorithm);
        String random= "xyz";
        assertThrows(ApiAccessException.class, () -> jwtTokenUtil.validate(random));
    }

    @Test
    void validateFail_DueToInvalidToken() {
        Algorithm algorithm = Algorithm.HMAC256("RANDOM_SECRET");
        Strategy strategy = Strategy.AUTO;
        when(JWTSecurityProperties.getStrategy()).thenReturn(strategy);
        when(algorithmStrategy.getAlgorithm(any(Strategy.class))).thenReturn(algorithm);
        String random = "xyz";
        assertThrows(ApiAccessException.class, () -> jwtTokenUtil.validate(random));
    }

    @Test
    void generateTokenPass() {
        when(JWTSecurityProperties.getStrategy()).thenReturn(Strategy.AUTO);
        when(algorithmStrategy.getAlgorithm(any(Strategy.class))).thenReturn(Algorithm.HMAC256("RANDOM_SECRET"));
        when(JWTSecurityProperties.getIssuer()).thenReturn("RANDOM_ISSUER");
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        User user = userTestDataFactory.getRandomUser();
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        PayloadDetails payloadDetails = new PayloadDetails(userPrincipal.getUsername(), userPrincipal.getProfileName());
        assertDoesNotThrow(() -> jwtTokenUtil.generateToken(payloadDetails));
    }

    @Test
    void generateTokenFail() {
        Algorithm algorithm = null;
        when(JWTSecurityProperties.getStrategy()).thenReturn(Strategy.ASYMMETRIC_ENCRYPTION);
        when(algorithmStrategy.getAlgorithm(any(Strategy.class))).thenReturn(algorithm);
        when(JWTSecurityProperties.getIssuer()).thenReturn("RANDOM_ISSUER");
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        User user = userTestDataFactory.getRandomUser();
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        PayloadDetails payloadDetails = new PayloadDetails(userPrincipal.getUsername(), userPrincipal.getProfileName());
        assertThrows(ApiAccessException.class, () -> jwtTokenUtil.generateToken(payloadDetails));
    }

    @Test
    void getSubjectFromTokenFail() {
        assertThrows(ApiAccessException.class, () -> jwtTokenUtil.getSubjectFromToken("xyz"));
    }

    @Test
    void extractTokenAndGetSubject() {
        MockHttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
        JwtTokenUtilImpl jwtTokenUtil1 = Mockito.spy(new JwtTokenUtilImpl(JWTSecurityProperties, algorithmStrategy));
        doReturn("xyz").when(jwtTokenUtil1).extractToken(request);
        doReturn("xyz").when(jwtTokenUtil1).getSubjectFromToken(anyString());
        assertEquals(jwtTokenUtil1.extractTokenAndGetSubject(request), "xyz");
    }

    @Test
    void extractToken() {
        MockHttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
        String randomString = "Bearer xyzxyzxyz";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer xyzxyzxyz");
        assertEquals(jwtTokenUtil.extractToken(request), randomString.split(" ")[1]);
    }
}