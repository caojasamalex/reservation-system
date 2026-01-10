package com.djokic.data;

import com.djokic.enumeration.RoleEnumeration;

import java.time.LocalDateTime;

public class User {
    private int id = -1;
    private String username;
    private String password;
    private String fullName;
    private RoleEnumeration role;
    private LocalDateTime createdAt;

    public User(){
    }

    public User(int id, String username, String password, String fullName, RoleEnumeration role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return id;
    }
    public void setUserId(int id) { this.id = id; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleEnumeration getRole() {
        return role;
    }

    public void setRole(RoleEnumeration role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User{")
                .append("id=")
                .append(this.id)
                .append(", username=")
                .append(this.username)
                .append(", fullName=")
                .append(this.fullName)
                .append(", role=")
                .append(this.role)
                .append(", createdAt=")
                .append(this.createdAt)
                .append("}");
        return sb.toString();
    }

}
