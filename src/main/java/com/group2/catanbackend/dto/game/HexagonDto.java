package com.group2.catanbackend.dto.game;

import com.group2.catanbackend.gamelogic.enums.HexagonType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HexagonDto{
    private HexagonType hexagonType;
    private ResourceDistribution resourceDistribution;
    private int value;
    private int id;
    private boolean hasRobber;
}
