package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.HexagonType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.model.Player;
import lombok.Getter;
import lombok.Setter;

public class Hexagon {
    @Getter
    private final int id;
    @Getter
    private final HexagonType hexagonType;
    @Getter
    private final ResourceDistribution distribution;
    @Getter
    private final int rollValue;
    @Getter
    private final Building[] buildings;
    @Getter
    private int numOfAdjacentBuildings = 0;
    @Getter
    @Setter
    private boolean hasRobber = false;
    public Hexagon(HexagonType type, ResourceDistribution distribution, int rollValue, int id, boolean hasRobber) {
        this.hexagonType = type;
        this.distribution = distribution;
        this.rollValue = rollValue;
        this.buildings = new Building[3];
        this.id=id;
        this.hasRobber = hasRobber;
    }

    public void distributeResources() {
        if(hasRobber)return;
        for (Building building : buildings) {
            if (building != null) {
                building.giveResources(distribution);
            }
        }
    }

    public void distributeResources(Player player) {
        if(hasRobber)return;
        for (Building building : buildings) {
            if (building != null && building.getPlayer() == player) {
                building.giveResources(distribution);
            }
        }
    }

    public void removeBuilding(Building building) {
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] == building) {
                buildings[i] = null;
                numOfAdjacentBuildings--;
                break;
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

}
