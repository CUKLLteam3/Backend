package com.example.cukllteam3.config;

import com.example.cukllteam3.utill.ThrottlingFilter; // ← ThrottlingFilter 경로에 맞게 수정
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> throttlingFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        ThrottlingFilter filter = new ThrottlingFilter(); // ✅ 직접 생성
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}

