package com.group2.catanbackend.dto.game;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the Basic Structure of a Stomp Message to the client
 */
@Builder
@Getter
@Setter
public class MessageDto {
    private MessageType type;
    private Object data;
}
