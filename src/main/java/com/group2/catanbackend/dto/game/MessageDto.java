package com.group2.catanbackend.dto.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

/**
 * Defines the Basic Structure of a Stomp Message to the client
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlayersInLobbyDto.class, name = MessageType.PLAYERS_CHANGED),
        @JsonSubTypes.Type(value = GameStartedDto.class, name = MessageType.GAME_STARTED)
})
@Getter
@Setter
public abstract class MessageDto {
    private String eventType;
}
