package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class MakeTradeOfferMoveDto extends GameMoveDto{
    public MakeTradeOfferMoveDto(int[] giveResources, int [] getResources, List<Integer> toPlayers) {
        this.giveResources = giveResources;
        this.getResources = getResources;
        this.toPlayers = toPlayers;
        this.setEventType(GameMoveType.MAKETRADEMOVE);
    }

    private int[] giveResources;
    private int[] getResources;
    private List<Integer> toPlayers;//change to PlayerID[]


}

