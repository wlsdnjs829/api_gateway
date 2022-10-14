package com.jinwon.api.gateway.infra.provider;

import com.jinwon.api.gateway.infra.enums.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * JWT 토큰 인증
 */
@Slf4j
@Component
@RefreshScope
public class JwtTokenProvider {

    @Value("${spring.security.oauth2.jwt.public}")
    private String publicPath;

    private static final String RSA = "RSA";

    /**
     * 유효한 토큰 여부 반환
     *
     * @param accessToken JWT 토큰
     */
    public boolean validateToken(String accessToken) {
        try {
            final Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(getPublicKey())
                    .parseClaimsJws(accessToken);

            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception ex) {
            log.error(JwtException.getMessageByExceptionClass(ex.getClass()));
            log.error(ExceptionUtils.getStackTrace(ex));
            return false;
        }
    }

    /* 공개 키 발급 */
    private PublicKey getPublicKey() {
        try {
            final Resource resource = new ClassPathResource(publicPath);
            final String publicKey = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            final byte[] publicBytes = Base64.decode(publicKey);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

}
