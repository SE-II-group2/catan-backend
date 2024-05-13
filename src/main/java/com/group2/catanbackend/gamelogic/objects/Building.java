package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.model.Player;
import lombok.Getter;

@Getter
public class Building extends Intersection {

    Player player;
    private final BuildingType type;
    private final int id;

    public Building(Player player, BuildingType type, int id) {
        this.player = player;
        this.type = type;
        this.id = id;
    }

    public void giveResources(ResourceDistribution distribution) {
        int[] resources = distribution.getDistribution();
        if(this.type == BuildingType.CITY){
            for (int i = 0; i < resources.length; i++) {
                resources[i] *= 2; // Multiply the resources by 2 for cities
            }
        }
        player.adjustResources(resources);
    }
}