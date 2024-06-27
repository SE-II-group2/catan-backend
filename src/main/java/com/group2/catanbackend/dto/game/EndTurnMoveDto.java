package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class EndTurnMoveDto extends GameMoveDto{

    public EndTurnMoveDto(IngamePlayerDto nextPlayer) {
        this.nextPlayer=nextPlayer;
    }

    private IngamePlayerDto nextPlayer;
}
