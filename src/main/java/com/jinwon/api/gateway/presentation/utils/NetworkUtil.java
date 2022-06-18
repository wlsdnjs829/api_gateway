package com.jinwon.api.gateway.presentation.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 네트워크 관련 Util
 */
@UtilityClass
public class NetworkUtil {

    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    private static final String REST = ",";

    public String getClientIp(ServerHttpRequest request) {
        final String clientIp = Optional.of(request)
                .map(HttpMessage::getHeaders)
                .map(httpHeaders -> httpHeaders.get(X_FORWARDED_FOR))
                .orElseGet(List::of)
                .stream()
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(clientIp)
                .map(ip -> ip.split(REST))
                .map(List::of)
                .orElseGet(Collections::emptyList)
                .stream()
                .findFirst()
                .map(String::trim)
                .orElse(StringUtils.EMPTY);
    }


}
