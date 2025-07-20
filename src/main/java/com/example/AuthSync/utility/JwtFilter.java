package com.example.AuthSync.utility;

import com.example.AuthSync.config.CustomUserDetailService;
import com.example.AuthSync.entity.UserEntity;
import com.example.AuthSync.service.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Log4j2
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
//            email = jwtService.extractUsername(jwtToken);
        }

        if(jwtToken==null){
            Cookie[] cookies=request.getCookies();
            if(cookies!=null){
                for(Cookie cookie:cookies){
                    if("jwtToken".equals(cookie.getName())){
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        // ✅ Only extract username if token is not null
        if (jwtToken != null) {
            try {
                email = jwtService.extractUsername(jwtToken);
                log.info("Extracted username: {}", email);  // DEBUG
            } catch (Exception e) {
                log.error("JWT extraction failed: {}", e.getMessage());
            }
        }
//        email=jwtService.extractUsername(jwtToken);
        log.info("Extracted username: {}", email);  // DEBUG
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails= customUserDetailService.loadUserByUsername(email);
            if (jwtService.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
