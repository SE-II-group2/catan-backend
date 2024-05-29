package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.Board;
import com.group2.catanbackend.model.Player;
import lombok.Getter;

@Getter
public class Connection {
    Player player;

    public boolean isNextToOwnRoad(Board board, Player player, int intersectionID){
        Connection[][] adjacencyMatrix = board.getAdjacencyMatrix();

        //check the specific intersection in the adjacencyMatrix if there are any roads, and if it belongs to the playerID who wants to build
        for(int i = 0; i < 54; i++){
            if((adjacencyMatrix[i][intersectionID] instanceof Road) && (adjacencyMatrix[i][intersectionID].getPlayer() == player)){
                return true;
            }
        }
        return false;
    }

    public boolean isNextToOwnBuilding(Board board, Player player, int intersectionID){
        Intersection[][] intersections = board.getIntersections();

        int[] fromIntersectionCoordinates = board.translateIntersectionToMatrixCoordinates(intersectionID);
        int row = fromIntersectionCoordinates[0];
        int col = fromIntersectionCoordinates[1];

        return intersections[row][col] instanceof Building && intersections[row][col].getPlayer() == player;
    }
}
