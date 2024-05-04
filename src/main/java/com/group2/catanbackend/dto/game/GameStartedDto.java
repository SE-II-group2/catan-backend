package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameStartedDto extends MessageDto{
    public GameStartedDto(){
        setEventType(MessageType.GAME_STARTED);
    }
}
