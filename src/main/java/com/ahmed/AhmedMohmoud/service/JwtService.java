package com.ahmed.AhmedMohmoud.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY;
    private final long jwtExpiration;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.expiration}") long jwtExpiration) {
        this.SECRET_KEY = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    private Key getSignInKey(){
        byte[] kb= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(kb);
    }
    public String buildToken(Map<String , Object> map , UserDetails us , long jwtExpiration){
        return Jwts.builder()
                .setClaims(map)
                .setSubject(us.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }
    public String generateToken(Map<String , Object> map , UserDetails us){
        return buildToken(map , us , jwtExpiration);
    }
    public String generateToken(UserDetails us){
        return generateToken(new HashMap<>() , us);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private <T> T extractClaim(String token , Function<Claims , T> fun){
        Claims claims=extractAllClaims(token);
        return fun.apply(claims);
    }
   public String getUserName(String token){
        return extractClaim(token,Claims::getSubject);
   }
   public Date getExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
   }
   public boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
   }
   public boolean isTokenValid(String token , UserDetails us){
        String username=getUserName(token);
      return !isTokenExpired(token) && username.equals(us.getUsername());
   }







}
