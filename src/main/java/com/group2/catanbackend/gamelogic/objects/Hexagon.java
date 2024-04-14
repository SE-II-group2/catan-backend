package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.Location;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import lombok.Getter;

public class Hexagon {
    @Getter
    private final Location type;
    @Getter
    private final ResourceDistribution distribution;
    @Getter
    private final int rollValue;
    @Getter
    private Building[] buildings;
    @Getter
    private int numOfAdjacentBuildings =0;

    public Hexagon(Location type, ResourceDistribution distribution, int rollValue) {
        this.type = type;
        this.distribution=distribution;
        this.rollValue = rollValue;
        this.buildings = new Building[3];
    }

    public void distributeResources() {
        for (Building building : buildings) {
            if (building != null) {
                building.giveResources(distribution);
            }
        }
    }

    public void addBuilding(Building building) {
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] == null) {
                buildings[i] = building;
                numOfAdjacentBuildings++;
                break;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Hexagon Type: %s; Rollvalue: %d; Number of Buildings adjecent: %d\n",type, rollValue, numOfAdjacentBuildings);
    }
}
