package com.group2.catanbackend.gamelogic.enums;

public enum ResourceCost {
    VILLAGE(new int[]{1, 1, 1, 1, 0}), // Requires 1 of each resource except Mountain
    CITY(new int[]{2, 0, 0, 0, 3}), // Requires 3 Mountain and 2 Fields
    DEVELOPMENT_CARD(new int[]{1, 1, 0, 0, 1}), // Requires 1 Field, 1 Pasture and 1 Mountain
    ROAD(new int[]{0, 0, 1, 1, 0}); // Requires 1 Forest and 1 Hill

    private final int[] cost;

    ResourceCost(int[] cost) {
        this.cost = cost;
    }

    public int[] getCost() {
        return cost;
    }
}

