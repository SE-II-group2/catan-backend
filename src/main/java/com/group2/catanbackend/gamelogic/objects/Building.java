package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import lombok.Getter;

public class Building extends Intersection {

    @Getter
    private final BuildingType type;

    public Building(int playerID, BuildingType type) {
        this.playerID = playerID;
        this.type = type;
    }

    public void giveResources(ResourceDistribution distribution) {
        int[] resources = distribution.getDistribution();
        if(this.type == BuildingType.CITY){
            for (int i = 0; i < resources.length; i++) {
                resources[i] *= 2; // Multiply the resources by 2 for cities
            }
        }
        //Give resources to the controlling player
    }

}