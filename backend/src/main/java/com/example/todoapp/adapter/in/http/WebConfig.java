package com.example.todoapp.adapter.in.http;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() { // via FilterRegistrationBean (not @Component) to avoid @WebMvcTes@WebMvcTestt auto-loading. So no mocking auth in backend tests needed.
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(new JwtFilter());
        registration.addUrlPatterns("/api/*");
        registration.setEnabled(false); // enabled once JwtFilter is implemented
        return registration;
    }
}
