package com.github.viniciusfcf.wildfly;

public class Jwt {
	
    private String token;

    public Jwt(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
