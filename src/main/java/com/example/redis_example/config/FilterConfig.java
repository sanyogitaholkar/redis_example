package com.example.redis_example.config;

import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.redis_example.filter.ClickSlidingWindowFilter;

@Configuration
public class FilterConfig {
    /*
     * if there are 2 filter and i want for this api or uri this filter should work
     * then ?
     */

    @Bean
    public FilterRegistrationBean<RateLimitFilter> filterARegistration() {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimitFilter()); // Your filter instance
        registration.addUrlPatterns("/c/ad-click"); // URL pattern
        registration.setOrder(1); // Execution order (optional)
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ClickSlidingWindowFilter> filterBRegistration() {
        FilterRegistrationBean<ClickSlidingWindowFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ClickSlidingWindowFilter()); // Your filter instance
        registration.addUrlPatterns("/ccc/ad-click"); // URL pattern
        registration.setOrder(2); // Order after FilterA
        return registration;
    }

}