package com.resumeanalyzer.authservice.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String fullName;
    private String role;
    private String message;

    public AuthResponse() {
        super();
    }

    public AuthResponse(String token, String email, String fullName, String role, String message) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}