package com.djokic.data;

import com.djokic.enumeration.RoleEnumeration;

import java.time.LocalDateTime;

public class EditUserRequest {
    private String username;
    private String password;
    private String fullName;

    public EditUserRequest() {
    }

    public EditUserRequest(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
