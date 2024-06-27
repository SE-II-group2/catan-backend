package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameProgressDto extends MessageDto{

    public GameProgressDto(GameMoveDto gameMoveDto) {
        this.gameMoveDto=gameMoveDto;

    }
    private GameMoveDto gameMoveDto;

}
