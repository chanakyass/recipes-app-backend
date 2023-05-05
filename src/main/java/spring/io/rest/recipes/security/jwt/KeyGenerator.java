package spring.io.rest.recipes.security.jwt;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class KeyGenerator {
    public RSAPrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
        byte[] encoded = Base64.decodeBase64(key);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) kf.generatePrivate(keySpec);
    }

    public RSAPublicKey getPublicKeyFromString(String key) throws GeneralSecurityException {
        byte[] encoded = Base64.decodeBase64(key);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
    }
}
