package com.group2.catanbackend.gamelogic.objects;

public class Building extends Intersection {

    public enum BuildingType { CITY, VILLAGE }
    BuildingType type;

    public Building(int playerID, BuildingType type) {
        this.playerID = playerID;
        this.type = type;
    }

    public void giveResources(int[] resources) {
        if(this.type == BuildingType.CITY){
            for (int i = 0; i < resources.length; i++) {
                resources[i] *= 2; // Multiply the resources by 2 for cities
            }
        }
    }

    public BuildingType getType() {
        return type;
    }
}