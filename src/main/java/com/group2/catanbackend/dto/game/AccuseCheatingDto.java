package com.group2.catanbackend.dto.game;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccuseCheatingDto extends GameMoveDto{

    public AccuseCheatingDto(IngamePlayerDto sender) {
        this.sender = sender;
    }

    private IngamePlayerDto sender;
}
