package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.enums.HexagonType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.model.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Hexagon {
    private final int id;
    private final HexagonType hexagonType;
    private final ResourceDistribution distribution;
    private final int rollValue;
    private final Building[] buildings;
    private int numOfAdjacentBuildings = 0;
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
                break;
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
