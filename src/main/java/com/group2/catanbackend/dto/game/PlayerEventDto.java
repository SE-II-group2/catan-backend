package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Describes an Event related to a player.
 */
public class PlayerEventDto {
    Type type;
    PlayerDto playerDto;
    public enum Type {
        PLAYER_JOINED,
        PLAYER_LEFT,
        //TODO: Connection state changed. IE socket Disconnect
    }
}
