package com.example.chat_app.responses;

public class LoginResponse {
    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }
    // Setter for token
    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    // Setter for expiresIn
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
