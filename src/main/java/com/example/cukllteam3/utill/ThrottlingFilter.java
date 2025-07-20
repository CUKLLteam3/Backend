package com.example.cukllteam3.utill;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class ThrottlingFilter implements Filter {

    // IP별 버킷 저장
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        // 10초에 5번 요청 허용
        Bandwidth limit = Bandwidth.classic(8, Refill.intervally(8, Duration.ofSeconds(10)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        String ip = httpRequest.getRemoteAddr(); // 클라이언트 IP 기준

        // ✅ Swagger 관련 요청은 필터 적용 제외
        if (uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response); // 요청 허용
        } else {
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.getWriter().write("🚫 Too many requests. Please try again later.");
        }

    }
}
