package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.gamelogic.enums.*;
import com.group2.catanbackend.gamelogic.objects.*;
import com.group2.catanbackend.model.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

    @Getter
    private List<Hexagon> hexagonList;
    @Getter
    private Connection[][] adjacencyMatrix;
    @Getter
    private Intersection [][] intersections;
    private int[][] surroundingHexagons;
    private int[][] connectedIntersections;
    private static final int NON_EXISTING_HEXAGON = 19;
    @Setter
    private boolean isSetupPhase = true;

    public Board(){
        generateHexagons();
        generateAdjacencyMatrix();
        generateIntersectionsStartingArray();
        generateSurroundingHexagonArray();
        generateConnectedIntersections();
    }

    public void distributeResourcesByDiceRoll(int diceRoll) {
        for (Hexagon hexagon : hexagonList) {
            if (hexagon.getRollValue() == diceRoll) {
                hexagon.distributeResources();
            }
        }
    }

    public boolean addNewRoad(Player player, int connectionID){
        //TODO: check if player has enough Resources

        // translate connection to two Intersections
        int[] connectionIntersections = getConnectedIntersections(connectionID);
        int fromIntersection = connectionIntersections[0];
        int toIntersection = connectionIntersections[1];

        if(isSetupPhase && adjacencyMatrix[fromIntersection][toIntersection] != null && !(adjacencyMatrix[fromIntersection][toIntersection] instanceof Road)){
            Road road = new Road(player);
            adjacencyMatrix[fromIntersection][toIntersection] = road;
            adjacencyMatrix[toIntersection][fromIntersection] = road;
            return true;
        }

        if(adjacencyMatrix[fromIntersection][toIntersection] != null && !(adjacencyMatrix[fromIntersection][toIntersection] instanceof Road) // check if null or road already
                && (isNextToOwnRoad(fromIntersection,player) || isNextToOwnRoad(toIntersection,player))){ //check if a road is next to one of the intersections
            Road road = new Road(player);
            adjacencyMatrix[fromIntersection][toIntersection] = road;
            adjacencyMatrix[toIntersection][fromIntersection] = road;
            return true;
        }
        return false;
    }

    public boolean addNewVillage(Player player, int intersectionID){
        //TODO: check if player has enough Resources

        int[] intersectionCoordinates = translateIntersectionToMatrixCoordinates(intersectionID);
        int row = intersectionCoordinates[0];
        int col = intersectionCoordinates[1];

        if(isSetupPhase && intersections[row][col] != null && noBuildingAdjacent(row, col) && !(intersections[row][col] instanceof Building)){
            intersections[row][col] = new Building(player,BuildingType.VILLAGE);
            Building village = (Building)intersections[row][col];
            addBuildingToSurroundingHexagons(intersectionID,village);
            return true;
        }

        if((intersections[row][col] != null) && !(intersections[row][col] instanceof Building) && noBuildingAdjacent(row,col) && isNextToOwnRoad(intersectionID,player)){
            intersections[row][col] = new Building(player,BuildingType.VILLAGE);
            Building village = (Building)intersections[row][col];

            addBuildingToSurroundingHexagons(intersectionID,village);
            return true;
        }
        return false;
    }

    public boolean addNewCity(Player player, int intersectionID){
        //TODO: check if player has enough Resources

        int[] intersectionCoordinates = translateIntersectionToMatrixCoordinates(intersectionID);
        int row = intersectionCoordinates[0];
        int col = intersectionCoordinates[1];

        if(intersections[row][col].getType() == BuildingType.VILLAGE && intersections[row][col].getPlayer() == player){
            intersections[row][col] = new Building(player,BuildingType.CITY);
            return true;
        }
        return false;
    }

    private void addBuildingToSurroundingHexagons(int intersection, Building building){
        int firstHexagon = surroundingHexagons[1][intersection];
        int secondHexagon = surroundingHexagons[2][intersection];
        int thirdHexagon = surroundingHexagons[3][intersection];

        if(firstHexagon != NON_EXISTING_HEXAGON){
            hexagonList.get(firstHexagon).addBuilding(building);
        }
        if(secondHexagon != NON_EXISTING_HEXAGON){
            hexagonList.get(secondHexagon).addBuilding(building);
        }
        if(thirdHexagon != NON_EXISTING_HEXAGON){
            hexagonList.get(thirdHexagon).addBuilding(building);
        }
    }

    public boolean noBuildingAdjacent(int row, int col){

        boolean evenCol = col % 2 == 0;
        boolean evenRow = row % 2 == 0;
        boolean nextToBuilding;
        if(col==0){
            nextToBuilding = (intersections[row][col + 1] instanceof Building);
        } else if (col==intersections[0].length) {
            nextToBuilding = (intersections[row][col - 1] instanceof Building);
        }else nextToBuilding = (intersections[row][col - 1] instanceof Building || intersections[row][col + 1] instanceof Building);

        if(nextToBuilding) {
            return false;
        }
        //if even even check or uneven uneven check below, else above if there is a building next to the position where it should be built
        if((evenRow && evenCol) || (!evenRow && !evenCol)){
            if(row!=0 && intersections[row-1][col] instanceof Building){
                nextToBuilding = true;
            }

        } else{
            if(row!= intersections[0].length && intersections[row + 1][col] instanceof Building) {
                nextToBuilding = true;
            }
        }

        return !nextToBuilding;
    }

    public boolean isNextToOwnRoad(int intersection, Player player){
        //check the specific intersection in the adjacencyMatrix if there are any roads, and if it belongs to the playerID who wants to build
        for(int i = 0; i < 54; i++){
            if((adjacencyMatrix[i][intersection] instanceof Road) && (adjacencyMatrix[i][intersection].getPlayer() == player)){
                return true;
            }
        }
        return false;
    }

    public int[] translateIntersectionToMatrixCoordinates(int intersectionID) {
        int[] coordinates = new int[2];
        int firstRowIntersections = 7;
        int secondRowIntersections = firstRowIntersections + 9;
        int thirdRowIntersections = secondRowIntersections + 11;
        int fourthRowIntersections = thirdRowIntersections + 11;
        int fifthRowIntersections = fourthRowIntersections + 9;

        if(intersectionID < firstRowIntersections){
            coordinates[1] = intersectionID + 2;
        } else if(intersectionID < secondRowIntersections){
            coordinates[0] = 1;
            coordinates[1] = intersectionID - firstRowIntersections + 1;
        } else if(intersectionID < thirdRowIntersections){
            coordinates[0] = 2;
            coordinates[1] = intersectionID - secondRowIntersections;
        } else if(intersectionID < fourthRowIntersections){
            coordinates[0] = 3;
            coordinates[1] = intersectionID - thirdRowIntersections;
        } else if(intersectionID < fifthRowIntersections){
            coordinates[0] = 4;
            coordinates[1] = intersectionID - fourthRowIntersections + 1;
        } else{
            coordinates[0] = 5;
            coordinates[1] = intersectionID - fifthRowIntersections + 2;
        }

        return coordinates;
    }

    private int[] getConnectedIntersections(int connectionID) {
        int[] connectionIntersections = new int[2];

        connectionIntersections[0] = connectedIntersections[0][connectionID];
        connectionIntersections[1] = connectedIntersections[1][connectionID];

        return connectionIntersections;
    }

    public void generateConnectedIntersections(){ // shows which connection is connected to which 2 intersections
        connectedIntersections = new int[2][72];
        connectedIntersections[0] = new int[] {0,1,2,3,4,5,0,2 ,4 ,6 ,7,8,9 ,10,11,12,13,14,7 ,9 ,11,13,15,16,17,18,19,20,21,22,23,24,25,28,27,30,29,32,31,34,33,36,35,16,18,20,22,24,26,39,38,41,40,43,42,45,44,28,30,32,34,36,48,47,50,49,52,51,39,41,43,45};
        connectedIntersections[1] = new int[] {1,2,3,4,5,6,8,10,12,14,8,9,10,11,12,13,14,15,17,19,21,23,25,17,18,19,20,21,22,23,24,25,26,29,28,31,30,33,32,35,34,37,36,27,29,31,33,35,37,40,39,42,41,44,43,46,45,38,40,42,44,46,49,48,51,50,53,52,47,49,51,53};
    }

    private void generateHexagons() {
        List<Location> locations = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy locations and values lists to ensure original lists remain unchanged (19 locations total)
        Collections.addAll(locations, Location.HILLS, Location.HILLS, Location.HILLS, Location.FOREST,
                Location.FOREST, Location.FOREST, Location.FOREST, Location.MOUNTAINS, Location.MOUNTAINS,
                Location.MOUNTAINS, Location.FIELDS, Location.FIELDS, Location.FIELDS, Location.FIELDS,
                Location.PASTURE, Location.PASTURE, Location.PASTURE, Location.PASTURE, Location.DESERT);
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        hexagonList = new ArrayList<>();

        Collections.shuffle(locations);
        Collections.shuffle(values);

        for (int i = 0; i<locations.size(); i++) {
            Location location = locations.get(i);
            int value;
            if (location == Location.DESERT) {
                value = 0; // Desert location should have value 0
            } else {
                value = values.remove(0);
            }

            ResourceDistribution resourceDistribution = switch (location) {
                case FIELDS -> ResourceDistribution.FIELDS;
                case PASTURE -> ResourceDistribution.PASTURE;
                case FOREST -> ResourceDistribution.FOREST;
                case HILLS -> ResourceDistribution.HILLS;
                case MOUNTAINS -> ResourceDistribution.MOUNTAINS;
                default -> ResourceDistribution.DESERT;
            };
            hexagonList.add(new Hexagon(location, resourceDistribution, value, i));
        }
    }

    private void generateAdjacencyMatrix() {
        Connection emptyConnection = new Connection();
        adjacencyMatrix = new Connection[54][54];

        int[]rows = {0,0,1,1,2,2,2 ,3,3,4,4,4 ,5,5,6 ,6,7 ,7,8,8,8,9,9 ,9 ,10,10,10,11,11,11,12,12,12,13,13,13,14,14,14,15,15,16,16,17,17,17,18,18,18,19,19,19,20,20,20,21,21,21,22,22,22,23,23,23,24,24,24,25,25,25,26,26,27,27,28,28,28,29,29,29,30,30,30,31,31,31,32,32,32,33,33,33,34,34,34,35,35,35,36,36,36,37,37,38,38,39,39,39,40,40,40,41,41,41,42,42,42,43,43,43,44,44,44,45,45,45,46,46,47,47,48,48,49,49,49,50,50,51,51,51,52,52,53,53};
        int[]cols = {1,8,0,2,1,3,10,2,4,3,5,12,4,6,5,14,8,17,0,7,9,8,10,19,2 ,9 ,11,10,12,21,4 ,11,13,12,14,23,6 ,13,15,14,25,17,27,7 ,16,18,17,19,29,9 ,18,20,19,21,31,11,20,22,21,23,33,13,22,24,23,25,35,15,24,26,25,37,16,28,27,29,38,18,28,30,29,31,40,20,30,32,31,33,42,22,32,34,33,35,44,24,34,36,35,37,46,26,36,28,39,38,40,47,30,39,41,40,42,49,32,41,43,42,44,51,34,43,45,44,46,53,36,45,39,48,47,49,41,48,50,49,51,43,50,52,51,53,45,52};

        for (int i = 0; i < rows.length; i++) {
            adjacencyMatrix[rows[i]][cols[i]] = emptyConnection;
        }
    }

    private void generateIntersectionsStartingArray() {
        Intersection intersection = new Intersection();
        intersections = new Intersection[6][11];

        //fill first 3 rows from the top and the last 3 from below at the same time
        for (int i = 0; i <= 2; i++) {
            for (int j = 2 - i; j <= 8 + i; j++) {
                intersections[i][j] = intersection;
                intersections[intersections.length - 1 - i][j] = intersection;
            }
        }
    }

    private void generateSurroundingHexagonArray() {
        surroundingHexagons = new int[4][54];
        surroundingHexagons[0] = new int[] {0 ,1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53};
        surroundingHexagons[1] = new int[] {0 ,0 ,0 ,1 ,1 ,2 ,2 ,3 ,3 ,3,0 ,1 ,1 ,2 ,2 ,6 ,7 ,7 ,7 ,3 ,4 ,5 ,5 ,5 ,6 ,6 ,11,7 ,7 ,7 ,8 ,8 ,9 ,9 ,10,10,11,11,12,12,12,13,13,14,14,15,15,16,16,16,17,17,18,18};
        surroundingHexagons[2] = new int[] {19,19,1 ,19,2 ,19,19,19,0 ,4,1 ,4 ,2 ,5 ,6 ,19,19,3 ,8 ,4 ,8 ,9 ,9 ,6 ,10,11,19,19,12,8 ,12,9 ,13,10,14,11,15,19,19,16,13,16,14,17,15,18,19,19,19,17,19,18,19,19};
        surroundingHexagons[3] = new int[] {19,19,19,19,19,19,19,19,19,0,4 ,5 ,5 ,6 ,19,19,19,19,3 ,8 ,9 ,9 ,10,10,11,19,19,19,19,12,13,13,14,14,15,15,19,19,19,19,16,17,17,18,18,19,19,19,19,19,19,19,19,19};
    }


}

