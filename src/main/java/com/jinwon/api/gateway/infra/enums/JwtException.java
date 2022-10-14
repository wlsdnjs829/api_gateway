package com.jinwon.api.gateway.infra.enums;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;

import java.util.Arrays;

/**
 * JWT Provider Exception Message Enum
 */
@Getter
public enum JwtException {

    SIGNATURE(SignatureException.class, TokenMessage.SIGNATURE),
    MALFORMED(MalformedJwtException.class, TokenMessage.MALFORMED),
    EXPIRED(ExpiredJwtException.class, TokenMessage.EXPIRED),
    UNSUPPORTED(UnsupportedJwtException.class, TokenMessage.UNSUPPORTED),
    ILLEGAL(IllegalArgumentException.class, TokenMessage.ILLEGAL);

    private final Class<?> exceptionClass;
    private final TokenMessage tokenMessage;

    private static final String DEFAULT_ERROR_MASSAGE = "Jwt token provider error";

    JwtException(Class<?> exceptionClass, TokenMessage tokenMessage) {
        this.exceptionClass = exceptionClass;
        this.tokenMessage = tokenMessage;
    }

    public static String getMessageByExceptionClass(Class<?> exceptionClass) {
        return Arrays.stream(JwtException.values())
                .filter(e -> e.getExceptionClass().equals(exceptionClass))
                .map(JwtException::getTokenMessage)
                .map(TokenMessage::getMessage)
                .findFirst()
                .orElse(DEFAULT_ERROR_MASSAGE);
    }

}
