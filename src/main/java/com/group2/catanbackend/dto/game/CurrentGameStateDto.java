package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurrentGameStateDto extends MessageDto{

    public CurrentGameStateDto(List<HexagonDto> hexagons, List<IntersectionDto> intersections, List<ConnectionDto> connections, List<IngamePlayerDto> players, IngamePlayerDto activePlayer,  boolean isSetupPhase) {
        this.hexagons = hexagons;
        this.intersections=intersections;
        this.connections = connections;
        this.players=players;
        this.isSetupPhase=isSetupPhase;
        this.activePlayer = activePlayer;
    }

    private List<HexagonDto> hexagons;
    private List<IntersectionDto> intersections;
    private List<ConnectionDto> connections;
    private List<IngamePlayerDto> players;
    private IngamePlayerDto activePlayer;
    private boolean isSetupPhase;
}



