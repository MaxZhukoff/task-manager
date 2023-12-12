package com.manager.util;

import com.manager.config.SecurityProperties;
import com.manager.model.AuthToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final SecurityProperties securityProperties;

    public AuthToken readToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(securityProperties.getSecret().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
        long id = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);
        if (username == null)
            throw new IllegalArgumentException("username is null");
        return new AuthToken(id, username);
    }

    public String createToken(AuthToken authToken) {
        return Jwts.builder()
                .setSubject(String.valueOf(authToken.userId()))
                .setIssuedAt(new Date())
                .setExpiration(Date.from(
                        Instant.now().plus(securityProperties.getTokenTtl()))
                )
                .claim("username", authToken.email())
                .signWith(Keys.hmacShaKeyFor(securityProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
