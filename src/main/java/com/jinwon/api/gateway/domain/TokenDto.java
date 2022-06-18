package com.jinwon.api.gateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    /* 회원 고유 번호 */
    private long userSn;

    /* Client IP */
    private String clientIp;

    /* 이름 */
    private String name;

    /* 이메일 */
    private String email;

}
