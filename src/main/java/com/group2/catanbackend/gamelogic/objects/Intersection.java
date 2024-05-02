package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.model.Player;
import lombok.Getter;

@Getter
public class Intersection {
    Player player;
    BuildingType type = BuildingType.EMPTY;
}
