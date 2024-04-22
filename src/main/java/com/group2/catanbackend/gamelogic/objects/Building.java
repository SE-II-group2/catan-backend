package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.model.Player;
import lombok.Getter;

public class Building extends Intersection {

    @Getter
    private final BuildingType type;
    @Getter
    private final Player owner;

    public Building(Player player, BuildingType type) {
        this.owner=player;
        this.type = type;
    }

    public void giveResources(ResourceDistribution distribution) {
        int[] resources = distribution.getDistribution();
        if(this.type == BuildingType.CITY){
            for (int i = 0; i < resources.length; i++) {
                resources[i] *= 2; // Multiply the resources by 2 for cities
            }
        }
        owner.adjustResources(resources);
    }

}