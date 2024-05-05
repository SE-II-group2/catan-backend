package com.group2.catanbackend.dto.game;

import lombok.Getter;

public class GameMoveValidResponse extends MessageDto{
    @Getter
    String message;
     public GameMoveValidResponse(String message){
         this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
         this.message=message;
     }
}
