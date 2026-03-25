package com.cybersoft.minibank.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtilHelper {
    @Value("${jwt.key}")
    private String secretKey;

    private final int expirationTime = 8 * 60 * 60 * 1000;

    //Tạo token và set thời gian 8 tiếng
    public String generateToken(String data) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + expirationTime);

        String jws = Jwts.builder().subject(data).expiration(expiryDate).signWith(key).compact();
        return jws;
    }
}
