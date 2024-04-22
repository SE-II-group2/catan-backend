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
    @Getter
    private final int[] resources = new int[]{0,0,0,0,0};

    public Player(String token, String displayName, String gameID){
        this.token = token;
        this.displayName = displayName;
        this.gameID = gameID;
    }

    public PlayerDto toPlayerDto(){
        return new PlayerDto(getDisplayName(), getInGameID(), playerState);
    }

    public void adjustResources(int[] resources){
        if(resources!=null&&resources.length == 5){
            for (int i = 0; i < resources.length; i++) {
                this.resources[i]+= resources[i];
            }
        }
    }

    public boolean resourcesSufficient(int[] resourceCost){
        if(resourceCost!=null&&resourceCost.length == 5){
            for (int i = 0; i < resourceCost.length; i++) {
                if(this.resources[i]+resourceCost[i]<0)return false;
            }
        }
        return true;
    }
}
