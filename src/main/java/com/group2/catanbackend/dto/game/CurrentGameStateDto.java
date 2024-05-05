package com.group2.catanbackend.dto.game;

import lombok.Getter;

import java.util.List;


public class HexagonListDto extends MessageDto{
    public HexagonListDto(List<HexagonDto> hexagons) {
        this.hexagons = hexagons;
        this.setEventType(MessageType.GAME_OBJECT);
    }

    @Getter
    private final List<HexagonDto> hexagons;
}



