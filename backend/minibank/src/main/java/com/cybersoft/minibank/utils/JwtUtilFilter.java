package com.cybersoft.minibank.utils;

import jakarta.servlet.Filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtUtilFilter extends OncePerRequestFilter{

    // Kiểm tra JWT + Xác thực User + Cho phép hoặc từ chối request
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    jakarta.servlet.http.HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
}
