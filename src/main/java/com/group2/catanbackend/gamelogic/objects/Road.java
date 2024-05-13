package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.model.Player;
import lombok.Getter;

public class Road extends Connection{
    @Getter
    private final int id;

    public Road(Player player, int id){
        this.player=player;
        this.id=id;
    }

}
