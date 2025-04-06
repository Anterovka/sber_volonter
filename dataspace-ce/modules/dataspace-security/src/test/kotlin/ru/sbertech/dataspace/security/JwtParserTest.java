package ru.sbertech.dataspace.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sbp.sbt.dataspacecore.security.utils.JwtHelper;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class JwtParserTest {

    protected static final String JWT_CREATED;

    static {
        try {
            String randomString = UUID.randomUUID().toString();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 10);

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair key = keyGen.generateKeyPair();
            PrivateKey privateKey = key.getPrivate();
            PublicKey publicKey = key.getPublic();
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            JWT_CREATED = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("array", Arrays.asList("four", "five", "six"))
                    .withClaim("stringField", "9876543210")
                    .withClaim("intField", 8)
                    .withClaim("object", Collections.singletonMap("inner", "innerValue"))
                    .withExpiresAt(cal.getTime())
                    .withKeyId(randomString)
                    .sign(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void jwtParserBadFormatExceptionTest() {
        Assertions.assertThrows(SecurityException.class, () -> JwtHelper.Companion.parseJwt("12345"));
    }

    @Test
    void jwtParserPositiveTest() throws IOException {
        Map<String, String> parsed = JwtHelper.Companion.parseJwt(JWT_CREATED);
        Assertions.assertEquals("'9876543210'", parsed.get("stringField"));
        Assertions.assertEquals("8", parsed.get("intField"));
        Assertions.assertEquals("['four','five','six']", parsed.get("array"));
        Assertions.assertEquals("'innerValue'", parsed.get("object.inner"));
    }
}
