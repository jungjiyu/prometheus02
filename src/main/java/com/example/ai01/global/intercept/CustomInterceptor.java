package com.example.ai01.global.intercept;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CustomInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;

    @Value("${jwt.secret}")
    private String secretKey;

    public CustomInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        // /metrics 경로에 대해서는 인터셉터를 통과하지 않도록 함
        if (path.startsWith("/metrics") || path.startsWith("/actuator")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            String userId = claims.getSubject();
            if (userId != null) {
                meterRegistry.counter("http.server.requests.user", "user_id", userId).increment();
            }
        }
        return true;

    }
}