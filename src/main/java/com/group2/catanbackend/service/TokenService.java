package com.group2.catanbackend.service;

import com.group2.catanbackend.model.Player;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Scope("singleton")
public class TokenService {

    private final Map<String, Player> registeredTokens = new ConcurrentHashMap<>();
    public String generateToken(){
        return UUID.randomUUID().toString();
    }

    public void pushToken(String token, Player player){
        registeredTokens.put(token, player);
    }

    public boolean validateToken(String token, String gameID){
        Player player = registeredTokens.get(token);
        return player != null && player.getGameID().equals(gameID);

    }

    public void revokeToken(String token){
        registeredTokens.remove(token);
    }

    public boolean tokenExists(String token){
        return registeredTokens.containsKey(token);
    }

    public Player getPlayerByToken(String token){
        return registeredTokens.get(token);
    }
}
