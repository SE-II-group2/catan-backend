package com.group2.catanbackend.model;

import com.group2.catanbackend.dto.game.PlayerDto;
import lombok.*;

@Getter
public class Player {
    private final String token;
    private final String displayName;
    private final String gameID;
    @Setter
    private Integer inGameID = null; //used for Player in GameState
    @Setter
    private PlayerState playerState;
    public Player(String token, String displayName, String gameID){
        this.token = token;
        this.displayName = displayName;
        this.gameID = gameID;
    }

    public PlayerDto toPlayerDto(){
        return new PlayerDto(getDisplayName(), getInGameID(), playerState);
    }

}
