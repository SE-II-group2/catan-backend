package com.group2.catanbackend.dto.game;

import com.group2.catanbackend.gamelogic.enums.Location;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class HexagonDto{
    @Getter
    private Location location;
    @Getter
    private ResourceDistribution resourceDistribution;
    @Getter
    private int value;
    @Getter
    private int id;
}
