package com.heima.app.gateway.filter;

import com.heima.app.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Description: Todo
 * Class Name: AuthorizeFilter
 * Date: 2023/7/3 15:36
 *
 * @author Hao
 * @version 1.1
 */
@Slf4j
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取 request 和 response 对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 2. 判断用户是否登录
        if (request.getURI().getPath().contains("/login")) {
            // 本身是登录请求，则直接放行
            return chain.filter(exchange);
        }

        // 3. 获取 token
        String token = request.getHeaders().getFirst("token");

        // 4. 判断该 token 是否存在
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 5. 判断该 token 是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            // 判断是否过期
            int res = AppJwtUtil.verifyToken(claimsBody);
            if (res == 1 || res == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 6. 放行
        return chain.filter(exchange);
    }

    /**
     * @Description: 优先级设置，值越小优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
