package com.cybersoft.minibank.filter;

import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Lấy header Authorization
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String decodeToken = jwtHelper.decodeToken(token);
            System.out.println("user decode token" + decodeToken);
            if(decodeToken != null) {

                //Chuyển kiểu chuỗi thành object
                ObjectMapper objectMapper = new ObjectMapper();
                UserDTO user = objectMapper.readValue(decodeToken, UserDTO.class);

                //Tạo ra list đại diện cho List.of()
                List<GrantedAuthority> list = new ArrayList<>();

                //xét quyền cho user
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());
                list.add(simpleGrantedAuthority);

                // Sinh ra cái thẻ
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user.getUsername(), "", List.of());

                // Đóng mộc cho cái thẻ
                SecurityContext  securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}