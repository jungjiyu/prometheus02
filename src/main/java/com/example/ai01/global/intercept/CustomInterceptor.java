package com.example.ai01.global.intercept;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        // /metrics 및 /actuator 경로를 인터셉터에서 제외
        if (path.startsWith("/metrics") || path.startsWith("/actuator")) return true;


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String userId = null;

            if (principal instanceof UserDetails)  userId = ((UserDetails) principal).getUsername();
            else userId = principal.toString();


            if (userId != null)  meterRegistry.counter("http.server.requests.user", "user_id", userId).increment();
        }


        return true;
    }
}