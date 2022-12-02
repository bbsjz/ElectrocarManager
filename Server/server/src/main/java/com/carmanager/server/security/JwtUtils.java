package com.carmanager.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtils {
    public static final long TOKEN_TIME=5*60*60*1000;

    @Value("${jwt.secret}")
    String secret;

    public Claims getClaims(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails)
    {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+TOKEN_TIME))
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    //验证用户是相同的，时间没有超过token的期限
    public boolean validateClaims(String token,UserDetails userDetails)
    {
        Claims claims=getClaims(token);
        return userDetails!=null&&
                claims.getSubject().equals(userDetails.getUsername())
                &&claims.getExpiration().after(new Date());
    }
}
