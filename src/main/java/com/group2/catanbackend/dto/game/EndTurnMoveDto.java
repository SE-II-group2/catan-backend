package com.group2.catanbackend.dto.game;

public class EndTurnMoveDto extends GameMoveDto{
    public EndTurnMoveDto() {
        this.setEventType(MessageType.GAME_MOVE);
    }
}
