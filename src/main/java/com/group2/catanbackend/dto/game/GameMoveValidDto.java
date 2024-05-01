package com.group2.catanbackend.dto.game;

import lombok.Getter;

public class GameMoveValidDto extends GameMoveDto{
    @Getter
    String message;
     public GameMoveValidDto(String message){
         this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
         this.message=message;
     }
}
