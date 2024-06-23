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

    public void distributeResourcesSetupPhase(Player player, int intersection) {
        int[] hexagonIDs = translateIntersectionToSurroundingHexagons(intersection);

        for (int hexagonID : hexagonIDs) {
            if(hexagonID != NON_EXISTING_HEXAGON){
                Hexagon hexagon = hexagonList.get(hexagonID);
                hexagon.distributeResources(player);
            }
        }
    }

    public boolean addNewRoad(Player player, int connectionID){
        int[] connectionIntersections = translateConnectionToIntersections(connectionID);
        int fromIntersection = connectionIntersections[0];
        int toIntersection = connectionIntersections[1];

        if(checkPossibleRoad(player,connectionID)){
            Road road = new Road(player,connectionID);
            adjacencyMatrix[fromIntersection][toIntersection] = road;
            adjacencyMatrix[toIntersection][fromIntersection] = road;
            return true;
        }
        return false;
    }

    private boolean checkPossibleRoad(Player player, int connectionID){
        int[] connectionIntersections = translateConnectionToIntersections(connectionID);
        int fromIntersection = connectionIntersections[0];
        int toIntersection = connectionIntersections[1];
        Connection connection = adjacencyMatrix[fromIntersection][toIntersection];

        if(isSetupPhase && connection != null && !(connection instanceof Road)
                && (connection.isNextToOwnBuilding(this,player,fromIntersection) || connection.isNextToOwnBuilding(this,player,toIntersection))){
            return true;
        }

        return connection != null && !(connection instanceof Road) // check if null or road already
                && (connection.isNextToOwnRoad(this, player, fromIntersection) || connection.isNextToOwnRoad(this, player, toIntersection));
    }

    public boolean addNewVillage(Player player, int intersectionID){
        int[] intersectionCoordinates = translateIntersectionToMatrixCoordinates(intersectionID);
        int row = intersectionCoordinates[0];
        int col = intersectionCoordinates[1];

        if(checkPossibleVillage(player,intersectionID)){
            intersections[row][col] = new Building(player, BuildingType.VILLAGE, intersectionID);
            Building village = (Building) intersections[row][col];
            addBuildingToSurroundingHexagons(intersectionID,village);
            return true;
        }
        return false;
    }

    public void moveRobber(int hexagonIDTarget){
        for(Hexagon hexagon : hexagonList){
            if(hexagon.isHasRobber())hexagon.setHasRobber(false);
            if(hexagon.getId()==hexagonIDTarget)hexagon.setHasRobber(true);
        }
    }

    private boolean checkPossibleVillage(Player player, int intersectionID){
        int[] intersectionCoordinates = translateIntersectionToMatrixCoordinates(intersectionID);
        int row = intersectionCoordinates[0];
        int col = intersectionCoordinates[1];

        Intersection intersection = intersections[row][col];

        if(isSetupPhase && intersection != null && intersection.notNextToBuilding(this, row, col) && !(intersections[row][col] instanceof Building)){
            return true;
        }

        return intersection != null && !(intersection instanceof Building) && intersection.notNextToBuilding(this, row, col)
                && intersection.isNextToOwnRoad(this, player, intersectionID);
    }

    public boolean addNewCity(Player player, int intersectionID){
        int[] intersectionCoordinates = translateIntersectionToMatrixCoordinates(intersectionID);
        int row = intersectionCoordinates[0];
        int col = intersectionCoordinates[1];

        if(checkPossibleCity(player,intersectionID)){
            removeBuildingFromSurroundingHexagons(intersectionID,(Building)intersections[row][col]);
            intersections[row][col] = new Building(player, BuildingType.CITY, intersectionID);
            Building city = (Building) intersections[row][col];
            addBuildingToSurroundingHexagons(intersectionID, city);
            return true;
        }
        return false;
    }

    private boolean checkPossibleCity(Player player, int intersectionID) {
        if(isSetupPhase){
            return false;
        }

        int[] intersectionCoordinates = translateIntersectionToMatrixCoordinates(intersectionID);
        int row = intersectionCoordinates[0];
        int col = intersectionCoordinates[1];

        return intersections[row][col].getType() == BuildingType.VILLAGE && intersections[row][col].getPlayer() == player;
    }

    public void removeBuildingFromSurroundingHexagons(int intersection, Building building) {
        int[] hexagons = translateIntersectionToSurroundingHexagons(intersection);

        for (int hexagon : hexagons) {
            if (hexagon != NON_EXISTING_HEXAGON) {
                hexagonList.get(hexagon).removeBuilding(building);
            }
        }
    }

    public void addBuildingToSurroundingHexagons(int intersection, Building building) {
        int[] hexagons = translateIntersectionToSurroundingHexagons(intersection);

        for (int hexagon : hexagons) {
            if (hexagon != NON_EXISTING_HEXAGON) {
                hexagonList.get(hexagon).addBuilding(building);
            }
        }
    }

    public int[] translateIntersectionToSurroundingHexagons(int intersection){
        int firstHexagon = surroundingHexagons[0][intersection];
        int secondHexagon = surroundingHexagons[1][intersection];
        int thirdHexagon = surroundingHexagons[2][intersection];
        return new int[]{firstHexagon,secondHexagon,thirdHexagon};
    }

    public int[] translateIntersectionToMatrixCoordinates(int intersectionID) {
        int[] coordinates = new int[2];
        int[] intersectionsPerRow = {7,9,11,11,9,7};
        int[] missingIntersectionsPerRow = {2,1,0,0,1,2};

        for (int i = 0; i < intersectionsPerRow.length; i++) {
            if (intersectionID < intersectionsPerRow[i]) {
                coordinates[0] = i;
                coordinates[1] = intersectionID + missingIntersectionsPerRow[i];
                break;
            }
            intersectionID -= intersectionsPerRow[i];
        }

        return coordinates;
    }

    private int[] translateConnectionToIntersections(int connectionID) {
        int[] connectionIntersections = new int[2];

        connectionIntersections[0] = connectedIntersections[0][connectionID];
        connectionIntersections[1] = connectedIntersections[1][connectionID];

        return connectionIntersections;
    }

    public int translateIntersectionsToConnection(int intersectionId1, int intersectionId2) {
        for (int connectionId = 0; connectionId < connectedIntersections[0].length; connectionId++) {
            if ((connectedIntersections[0][connectionId] == intersectionId1 && connectedIntersections[1][connectionId] == intersectionId2) ||
                    (connectedIntersections[0][connectionId] == intersectionId2 && connectedIntersections[1][connectionId] == intersectionId1)) {
                return connectionId;
            }
        }
        return -1; // Return -1 if no matching connection is found
    }


    public void generateConnectedIntersections(){ // shows which connection is connected to which 2 intersections
        connectedIntersections = new int[2][72];
        connectedIntersections[0] = new int[] {0,1,2,3,4,5,0,2 ,4 ,6 ,7,8,9 ,10,11,12,13,14,7 ,9 ,11,13,15,16,17,18,19,20,21,22,23,24,25,28,27,30,29,32,31,34,33,36,35,16,18,20,22,24,26,39,38,41,40,43,42,45,44,28,30,32,34,36,48,47,50,49,52,51,39,41,43,45};
        connectedIntersections[1] = new int[] {1,2,3,4,5,6,8,10,12,14,8,9,10,11,12,13,14,15,17,19,21,23,25,17,18,19,20,21,22,23,24,25,26,29,28,31,30,33,32,35,34,37,36,27,29,31,33,35,37,40,39,42,41,44,43,46,45,38,40,42,44,46,49,48,51,50,53,52,47,49,51,53};
    }

    private void generateHexagons() {
        List<HexagonType> hexagonTypes = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy hexagonTypes and values lists to ensure original lists remain unchanged (19 hexagonTypes total)
        Collections.addAll(hexagonTypes, HexagonType.HILLS, HexagonType.HILLS, HexagonType.HILLS, HexagonType.FOREST,
                HexagonType.FOREST, HexagonType.FOREST, HexagonType.FOREST, HexagonType.MOUNTAINS, HexagonType.MOUNTAINS,
                HexagonType.MOUNTAINS, HexagonType.FIELDS, HexagonType.FIELDS, HexagonType.FIELDS, HexagonType.FIELDS,
                HexagonType.PASTURE, HexagonType.PASTURE, HexagonType.PASTURE, HexagonType.PASTURE, HexagonType.DESERT);
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        hexagonList = new ArrayList<>();

        Collections.shuffle(hexagonTypes);
        Collections.shuffle(values);

        for (int i = 0; i< hexagonTypes.size(); i++) {
            HexagonType hexagonType = hexagonTypes.get(i);
            int value;
            boolean hasRobber=false;
            if (hexagonType == HexagonType.DESERT) {
                value = 0; // Desert hexagonType should have value 0
                hasRobber=true;
            } else {
                value = values.remove(0);
            }

            ResourceDistribution resourceDistribution = switch (hexagonType) {
                case FIELDS -> ResourceDistribution.FIELDS;
                case PASTURE -> ResourceDistribution.PASTURE;
                case FOREST -> ResourceDistribution.FOREST;
                case HILLS -> ResourceDistribution.HILLS;
                case MOUNTAINS -> ResourceDistribution.MOUNTAINS;
                default -> ResourceDistribution.DESERT;
            };
            hexagonList.add(new Hexagon(hexagonType, resourceDistribution, value, i, hasRobber));
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
        surroundingHexagons = new int[3][54];
        surroundingHexagons[0] = new int[] {0 ,0 ,0 ,1 ,1 ,2 ,2 ,3 ,3 ,3,0 ,1 ,1 ,2 ,2 ,6 ,7 ,7 ,7 ,3 ,4 ,5 ,5 ,5 ,6 ,6 ,11,7 ,7 ,7 ,8 ,8 ,9 ,9 ,10,10,11,11,12,12,12,13,13,14,14,15,15,16,16,16,17,17,18,18};
        surroundingHexagons[1] = new int[] {19,19,1 ,19,2 ,19,19,19,0 ,4,1 ,4 ,2 ,5 ,6 ,19,19,3 ,8 ,4 ,8 ,9 ,9 ,6 ,10,11,19,19,12,8 ,12,9 ,13,10,14,11,15,19,19,16,13,16,14,17,15,18,19,19,19,17,19,18,19,19};
        surroundingHexagons[2] = new int[] {19,19,19,19,19,19,19,19,19,0,4 ,5 ,5 ,6 ,19,19,19,19,3 ,8 ,9 ,9 ,10,10,11,19,19,19,19,12,13,13,14,14,15,15,19,19,19,19,16,17,17,18,18,19,19,19,19,19,19,19,19,19};
    }


}

