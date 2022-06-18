package com.jinwon.api.gateway.presentation.filter;

import com.jinwon.api.gateway.domain.TokenDto;
import com.jinwon.api.gateway.infra.component.TokenRedisComponent;
import com.jinwon.api.gateway.infra.provider.JwtTokenProvider;
import com.jinwon.api.gateway.presentation.utils.NetworkUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {

    private final JwtTokenProvider tokenProvider;
    private final TokenRedisComponent tokenRedisComponent;

    private static final String JWT_PREFIX = "Bearer ";
    private static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";

    public CustomAuthFilter(JwtTokenProvider tokenProvider, TokenRedisComponent tokenRedisComponent) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
        this.tokenRedisComponent = tokenRedisComponent;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();

            final String jwt = getJwtFromRequest(request);
            final String clientIp = NetworkUtil.getClientIp(request);

            if (!StringUtils.hasText(jwt) || !tokenProvider.validateToken(jwt)) {
                return handleForbidden(exchange);
            }

            final Optional<TokenDto> tokenOp = tokenRedisComponent.getTokenUser(jwt);

            if (tokenOp.isEmpty()) {
                return handleForbidden(exchange);
            }

            final String userClientIp = tokenOp.map(TokenDto::getClientIp)
                    .orElse(org.apache.commons.lang3.StringUtils.EMPTY);

            if (!org.apache.commons.lang3.StringUtils.equals(userClientIp, clientIp)) {
                return handleForbidden(exchange);
            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleForbidden(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /* JWT 토큰 조회 */
    private String getJwtFromRequest(ServerHttpRequest request) {
        final String bearerToken = Optional.of(request)
                .map(HttpMessage::getHeaders)
                .map(httpHeaders -> httpHeaders.get(X_AUTH_TOKEN))
                .orElseGet(List::of)
                .stream()
                .findFirst()
                .orElse(null);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_PREFIX)) {
            return bearerToken.replace(JWT_PREFIX, org.apache.commons.lang3.StringUtils.EMPTY);
        }

        return null;
    }

    public static class Config {

    }
}
