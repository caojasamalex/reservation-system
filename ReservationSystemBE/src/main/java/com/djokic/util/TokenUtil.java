package com.djokic.util;

import com.djokic.enumeration.RoleEnumeration;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class TokenUtil {

    public static String generateToken(int userId, String username, String role) {
        String payload = userId + ":" + username + ":" + role + ":" + Instant.now().getEpochSecond();
        
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public static TokenData parseToken(String token) {
        try{
            byte[] decoded = Base64.getDecoder().decode(token);
            String payload = new String(decoded, StandardCharsets.UTF_8);

            String[] parts = payload.split(":");
            if(parts.length != 4) {
                throw new IllegalArgumentException("Invalid token");
            }

            return new TokenData(
                    Integer.parseInt(parts[0]),
                    parts[1],
                    RoleEnumeration.valueOf(parts[2]),
                    Long.parseLong(parts[3])
            );
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid token");
        }
    }
}
