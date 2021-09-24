package spring.io.rest.recipes.unittests.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.io.rest.recipes.config.JWTSecurityProperties;
import spring.io.rest.recipes.enums.Strategy;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.security.jwt.AlgorithmStrategy;
import spring.io.rest.recipes.security.jwt.KeyGenerator;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlgorithmStrategyTest {

    @Mock
    private JWTSecurityProperties JWTSecurityProperties;
    @Mock
    private KeyGenerator keyGenerator;

    @InjectMocks
    private AlgorithmStrategy algorithmStrategy;

    private String mockString;

    @BeforeEach
    void setUp() {

        mockString = new Random().ints(97, 123)
                .limit(12)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }

    @Test
    void checkAlgorithmReturnedSuccessfully() throws GeneralSecurityException {
        when(JWTSecurityProperties.getSecretKey()).thenReturn(mockString);
        when(JWTSecurityProperties.getPrivateKey()).thenReturn(mockString);
        when(JWTSecurityProperties.getPublicKey()).thenReturn(mockString);
        assertNotNull(algorithmStrategy.getAlgorithm(Strategy.AUTO));
        assertNotNull(algorithmStrategy.getAlgorithm(Strategy.SYMMETRIC_ENCRYPTION));

        RSAPrivateKey rsaPrivateKey = Mockito.mock(RSAPrivateKey.class);
        RSAPublicKey rsaPublicKey = Mockito.mock(RSAPublicKey.class);

        when(keyGenerator.getPrivateKeyFromString(anyString())).thenReturn(rsaPrivateKey);
        when(keyGenerator.getPublicKeyFromString(anyString())).thenReturn(rsaPublicKey);
        assertNotNull(algorithmStrategy.getAlgorithm(Strategy.ASYMMETRIC_ENCRYPTION));
    }

    @Test
    void checkResultWhenGetPublicKeyThrowsException() throws GeneralSecurityException {
        when(JWTSecurityProperties.getPublicKey()).thenReturn(mockString);
        when(keyGenerator.getPublicKeyFromString(anyString())).thenThrow(GeneralSecurityException.class);
        assertThrows(ApiAccessException.class, () -> algorithmStrategy.getAlgorithm(Strategy.ASYMMETRIC_ENCRYPTION));
    }

    @Test
    void checkResultWhenGetPrivateKeyThrowsException() throws GeneralSecurityException {
        when(JWTSecurityProperties.getPrivateKey()).thenReturn(mockString);
        when(keyGenerator.getPrivateKeyFromString(anyString())).thenThrow(GeneralSecurityException.class);
        assertThrows(ApiAccessException.class, () -> algorithmStrategy.getAlgorithm(Strategy.ASYMMETRIC_ENCRYPTION));
    }

}