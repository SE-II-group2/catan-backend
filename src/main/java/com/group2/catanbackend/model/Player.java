package com.group2.catanbackend.model;

import lombok.*;

@Getter
public class Player {
    private final String token;
    private final String displayName;
    private final String gameID;
    @Setter
    private Integer inGameID = null; //used for Player in GameState
    //TODO: Connection State -> should represent the current state of the socket connection. DISCONNECTED, CONNECTED

    public Player(String token, String displayName, String gameID){
        this.token = token;
        this.displayName = displayName;
        this.gameID = gameID;
    }
}
