package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CurrentGameStateDto extends MessageDto{
    public CurrentGameStateDto(List<HexagonDto> hexagons, List<IntersectionDto> intersections, List<ConnectionDto> connections, List<IngamePlayerDto> playerOrder, boolean isSetupPhase) {
        this.hexagons = hexagons;
        this.intersections=intersections;
        this.connections = connections;
        this.playerOrder=playerOrder;
        this.setEventType(MessageType.GAME_OBJECT);
        this.isSetupPhase=isSetupPhase;
    }

    private List<HexagonDto> hexagons;
    private List<IntersectionDto> intersections;
    private List<ConnectionDto> connections;
    private List<IngamePlayerDto> playerOrder;
    private boolean isSetupPhase;
}



