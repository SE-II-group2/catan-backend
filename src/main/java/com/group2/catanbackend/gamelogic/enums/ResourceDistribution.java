package com.group2.catanbackend.gamelogic.enums;

public enum ResourceDistribution {
    FIELDS(new int[]{1, 0, 0, 0, 0}),
    PASTURE(new int[]{0, 1, 0, 0, 0}),
    FOREST(new int[]{0, 0, 1, 0, 0}),
    HILLS(new int[]{0, 0, 0, 1, 0}),
    MOUNTAINS(new int[]{0, 0, 0, 0, 1}),
    DESERT(new int[]{0, 0, 0, 0, 0});

    private final int[] distribution;

    ResourceDistribution(int[] distribution) {
        this.distribution = distribution;
    }

    public int[] getDistribution() {
        return distribution;
    }
}
