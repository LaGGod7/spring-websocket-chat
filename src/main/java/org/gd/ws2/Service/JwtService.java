package org.gd.ws2.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final String secret = "my-super-secret-key-my-super-secret-key";

    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
    public boolean isTokenValid(String token){
        try {
            extractUsername(token);
            System.out.println("JWT VALID");
            return true;
        }
        catch (Exception e){
            System.out.println("JWT INVALID: " + e.getMessage());
            return false;
        }
    }
}
