package com.group2.catanbackend.dto.game;

public class GameOverDto extends MessageDto{
    public GameOverDto() {
        this.setEventType(MessageType.GAME_OVER);
    }
}
