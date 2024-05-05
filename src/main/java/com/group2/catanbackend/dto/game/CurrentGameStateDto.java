package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class CurrentGameStateDto extends MessageDto{
    public CurrentGameStateDto(List<HexagonDto> hexagons, List<IntersectionDto> intersections, List<ConnectionDto> connections) {
        this.hexagons = hexagons;
        this.intersections=intersections;
        this.connections = connections;
        this.setEventType(MessageType.GAME_OBJECT);
    }

    @Getter
    private List<HexagonDto> hexagons;
    @Getter
    private List<IntersectionDto> intersections;
    @Getter
    private List<ConnectionDto> connections;
}



