package com.group2.catanbackend.gamelogic.objects;

public class Hexagon {
    private final String type;
    private final int[] resourceValue;
    private final int rollValue;
    private Building[] buildings;
    private int numOfAdjacentBuildings =0;


    public Hexagon(String type, int[] resourceValue, int rollValue) {
        this.type = type;
        this.resourceValue = resourceValue;
        this.rollValue = rollValue;
        this.buildings = new Building[3];
    }

    public void distributeResources() {
        for (Building building : buildings) {
            if (building != null) {
                building.giveResources(resourceValue);
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
    public String getType() {
        return type;
    }

    public int[] getResourceValue() {
        return resourceValue;
    }

    public int getRollValue() {
        return rollValue;
    }

    public Building[] getBuildings() {
        return buildings;
    }

    public int getNumOfAdjacentBuildings(){
        return numOfAdjacentBuildings;
    }

    @Override
    public String toString() {
        return String.format("Hexagon Type: %s; Rollvalue: %d; Number of Buildings adjecent: %d\n",type, rollValue, numOfAdjacentBuildings);
    }
}
