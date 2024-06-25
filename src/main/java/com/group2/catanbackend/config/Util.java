package com.group2.catanbackend.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;

public class Util {
    private Util(){}
    public static String extractToken(ServerHttpRequest request){
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(token == null){
            String[] parts = request.getURI().toString().split("\\?");
            if(parts.length == 2){
                return parts[1];
            }
        }
        return token;
    }
}
