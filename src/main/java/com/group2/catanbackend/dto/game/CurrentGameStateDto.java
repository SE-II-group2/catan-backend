package com.group2.catanbackend.dto.game;

import lombok.Getter;

import java.util.List;


public class CurrentGameStateDto extends MessageDto{
    public CurrentGameStateDto(List<HexagonDto> hexagons, List<IntersectionDto> intersections, List<ConnectionDto> connections) {
        this.hexagons = hexagons;
        this.intersections=intersections;
        this.connections = connections;
        this.setEventType(MessageType.GAME_OBJECT);
    }

    @Getter
    private final List<HexagonDto> hexagons;
    @Getter
    private final List<IntersectionDto> intersections;
    @Getter
    private final List<ConnectionDto> connections;
}



