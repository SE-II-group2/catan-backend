package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TradeMoveDto extends GameMoveDto{
    public TradeMoveDto(int[] resources, boolean[] toPlayer, int waitTime) {
        this.resources = resources;
        this.toPlayer = toPlayer;
        this.waitTime = waitTime;
        this.setEventType(GameMoveType.TRADEMOVE);
    }

    private int[] resources;
    private boolean[] toPlayer;
    private int waitTime;


}

