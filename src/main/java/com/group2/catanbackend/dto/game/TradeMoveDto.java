package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TradeMoveDto extends GameMoveDto{
    public TradeMoveDto(int[] giveResources, int [] getResources, int[] toPlayer) {
        this.giveResources = giveResources;
        this.getResources = getResources;
        this.toPlayer = toPlayer;
        this.setEventType(GameMoveType.TRADEMOVE);
    }

    private int[] giveResources;
    private int[] getResources;
    private int[] toPlayer;//change to PlayerID[]


}

