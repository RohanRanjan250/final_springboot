package com.shopping.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Value("${rate-limit.capacity:100}")
    private int capacity;
    
    @Value("${rate-limit.refill-tokens:100}")
    private int refillTokens;
    
    @Value("${rate-limit.refill-duration:60}")
    private int refillDuration;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = getClientKey(request);
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket());
        
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return false;
        }
    }
    
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillTokens, Duration.ofSeconds(refillDuration)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    private String getClientKey(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return clientIp + "_" + (userAgent != null ? userAgent.hashCode() : "");
    }
}
