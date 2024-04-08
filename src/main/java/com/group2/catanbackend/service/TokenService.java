package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.JoinRequestDto;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Scope("singleton")
public class TokenService {

    private final Map<String, JoinRequestDto> registeredTokens = new HashMap<>();
    public String generateToken(){
        return UUID.randomUUID().toString();
    }

    public void pushToken(String token, JoinRequestDto dto){
        registeredTokens.put(token, dto);
    }

    public boolean validateToken(String token, String gameID){
        JoinRequestDto tokenData = registeredTokens.get(token);
        return tokenData != null && tokenData.getGameID().equals(gameID);

    }

    public boolean tokenExists(String token){
        return registeredTokens.containsKey(token);
    }

    public JoinRequestDto getTokenMetadata(String token){
        return registeredTokens.get(token);
    }
}
