package com.djokic.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacSHA256 {
    private final String secretKey;

    public HmacSHA256() {
        this.secretKey = System.getProperty("HMAC_SECRET");

        if (this.secretKey == null) {
            throw new RuntimeException("HMAC_SECRET not set in .env");
        }
    }

    public String hashPassword(String password) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }
}
