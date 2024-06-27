package com.group2.catanbackend.dto.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

/**
 * Defines the Basic Structure of a Stomp Message to the client
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlayersInLobbyDto.class, name = MessageType.PLAYERS_CHANGED),
        @JsonSubTypes.Type(value = GameStartedDto.class, name = MessageType.GAME_STARTED),
        @JsonSubTypes.Type(value = CurrentGameStateDto.class, name = MessageType.GAME_OBJECT),
        @JsonSubTypes.Type(value = GameProgressDto.class, name = MessageType.GAME_MOVE_NOTIFIER),
        @JsonSubTypes.Type(value = TradeOfferDto.class, name = MessageType.TRADE_OFFERED),
        @JsonSubTypes.Type(value = GameOverDto.class, name = MessageType.GAME_OVER)
})
@Getter
public abstract class MessageDto {
    private final String eventType;
    public MessageDto(String eventType){
        this.eventType = eventType;
    }
    public String getEventType(){
        return eventType;
    }
}
