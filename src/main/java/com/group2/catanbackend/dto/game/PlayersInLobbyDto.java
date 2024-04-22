package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayersInLobbyDto extends MessageDto {
    List<PlayerDto> players;
    PlayerDto admin;
    PlayerEventDto event;

    public PlayersInLobbyDto(){
        super();
        setEventType(MessageType.PLAYERS_CHANGED);
    }
}
