package com.csust.hackathonserver.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    public static final String SECRET = "csust-hackathon-server-secret-key-please-change";
    private static final long EXPIRE_MS = 7200 * 1000L;
    public String generateToken(Long userId, String role) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public Long getUserId(String token) {
        String userId = JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getSubject();

        return Long.valueOf(userId);
    }
}
