package com.jinwon.api.gateway.infra.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 클라이언트 용 Token Message Enum
 */
@Getter
public enum TokenMessage {

    SIGNATURE(HttpStatus.FORBIDDEN, "유효하지 않은 시그니쳐"),
    MALFORMED(HttpStatus.FORBIDDEN, "유효하지 않은 JWT"),
    EXPIRED(HttpStatus.FORBIDDEN, "만료된 JWT"),
    UNSUPPORTED(HttpStatus.FORBIDDEN, "지원하지 않는 JWT"),
    ILLEGAL(HttpStatus.BAD_REQUEST, "존재하지 않는 JWT 내부 정보"),
    NON_EXPIRED(HttpStatus.BAD_REQUEST, "만료되지 않은 JWT"),
    MALFORMED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 JWT"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "만료된 REFRASH TOKEN"),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 정보"),
    NON_MATCH_USER_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호"),
    PARSER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파싱 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    TokenMessage(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
