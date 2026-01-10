package com.djokic.util;

import com.djokic.enumeration.RoleEnumeration;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class TokenUtil {
    private static final long TOKEN_TTL_SECONDS = 3600;

    public static String generateToken(int userId, String username, String role) {
        String payload = userId + ":" + username + ":" + role + ":" + Instant.now().getEpochSecond();
        
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    private static TokenData getAuthData(String authHeader) throws Exception {
        if(authHeader == null || !authHeader.startsWith("Bearer ")){ return null; }

        String token = authHeader.substring(7);

        TokenData tokenData = TokenUtil.parseToken(token);
        if (tokenData == null) {
            throw new Exception("Invalid token");
        }

        if(Instant.now().getEpochSecond() - tokenData.getIssuedAt() >= TOKEN_TTL_SECONDS){
            throw new Exception("Token expired!");
        }

        return tokenData;
    }

    private static TokenData parseToken(String token) {
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

    public static TokenData authorize(String authHeader) throws Exception {
        TokenData tokenData = getAuthData(authHeader);
        if(tokenData == null){
            throw new Exception("Invalid token!");
        }

        return tokenData;
    }

    public void checkOwnerOrAdmin(int resourceOwnerId, TokenData tokenData) throws Exception {
        if(resourceOwnerId <= 0){
            throw new Exception("Invalid resourceOwnerId!");
        }

        if(tokenData == null){
            throw new Exception("Invalid token!");
        }

        if(tokenData.getUserId() != resourceOwnerId && !tokenData.getRole().equals(RoleEnumeration.ADMIN)){
            throw new Exception("Forbidden!");
        }
    }
}
