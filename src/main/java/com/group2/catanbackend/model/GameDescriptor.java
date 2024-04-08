package com.group2.catanbackend.model;

import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.GameFullException;
import com.group2.catanbackend.exception.PlayerAlreadyInGameException;
import lombok.Getter;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class GameDescriptor {
    private final HashSet<Player> players;
    @Getter
    private final String id;
    @Getter
    private String adminToken;
    private final Date createdAt;

    public GameDescriptor(){
        this.players = new HashSet<>();
        this.id = UUID.randomUUID().toString().substring(0,7);
        this.createdAt = new Date();
    }


    public void join(Player player){
        if(players.contains(player))
            throw new PlayerAlreadyInGameException(ErrorCode.ERROR_PLAYER_ALREADY_IN_GAME);
        if(players.size() >= 4)
            throw new GameFullException(ErrorCode.ERROR_GAME_FULL + id);
        if(players.isEmpty()){
            adminToken = player.getToken();
        }
        players.add(player);
    }

    public int getPlayerCount(){
        return players.size();
    }
}
