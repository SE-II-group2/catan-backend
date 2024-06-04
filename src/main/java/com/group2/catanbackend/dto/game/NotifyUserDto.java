package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyUserDto extends MessageDto{

    public NotifyUserDto(String message) {
        this.setEventType(MessageType.PLAYER_NOTIFY);
        this.message=message;

    }
    private String message;

}
