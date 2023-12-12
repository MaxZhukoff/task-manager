package com.manager.util;

import com.manager.model.AuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            readTokenIfProvided(req);
        } catch (Exception e) {
            log.error("Cannot parse token", e);
        }
        chain.doFilter(req, res);
    }

    private void readTokenIfProvided(HttpServletRequest req) {
        String tokenString = req.getHeader("Authorization");
        if (tokenString == null || !tokenString.startsWith("Bearer "))
            return;
        String token = tokenString.substring("Bearer ".length());
        AuthToken authToken = jwtUtil.readToken(token);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authToken, "", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("User {} authorized", authToken.email());
    }
}
