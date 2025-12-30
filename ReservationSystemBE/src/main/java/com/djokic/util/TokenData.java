package com.djokic.util;

import com.djokic.enumeration.RoleEnumeration;

public class TokenData {
    private final int userId;
    private final String username;
    private final RoleEnumeration role;
    private final long issuedAt;

    public TokenData(int userId, String username, RoleEnumeration role, long issuedAt) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.issuedAt = issuedAt;
    }

    public int getUserId() { return this.userId; }
    public String getUsername() { return this.username; }
    public RoleEnumeration getRole() { return this.role; }
    public long getIssuedAt() { return this.issuedAt; }
}
