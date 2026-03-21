package com.cybersoft.minibank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Cấu hình config
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/auth/*").permitAll();
                    // Cho phép đường dẫn có quyền ADMIN truy cập
                    request.requestMatchers(HttpMethod.GET , "/*").hasRole("ADMIN");
                    // cho phép tất cả đường dẫn truy cập
                    request.anyRequest().authenticated();
                })
                .build();
    }
}
