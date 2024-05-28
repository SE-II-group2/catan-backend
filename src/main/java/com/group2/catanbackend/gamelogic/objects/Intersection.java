package com.group2.catanbackend.gamelogic.objects;

import com.group2.catanbackend.gamelogic.Board;
import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.model.Player;
import lombok.Getter;

@Getter
public class Intersection {
    Player player;
    BuildingType type = BuildingType.EMPTY;

    public boolean isNextToOwnRoad(Board board, Player player, int intersectionID){
        //check the specific intersection in the adjacencyMatrix if there are any roads, and if it belongs to the playerID who wants to build
        for(int i = 0; i < 54; i++){
            if((board.getAdjacencyMatrix()[i][intersectionID] instanceof Road) && (board.getAdjacencyMatrix()[i][intersectionID].getPlayer() == player)){
                return true;
            }
        }
        return false;
    }

    public boolean notNextToBuilding(Board board, int row, int col){
        Intersection[][] intersections = board.getIntersections();

        boolean evenCol = col % 2 == 0;
        boolean evenRow = row % 2 == 0;
        boolean nextToBuilding;

        if(col == 0){
            nextToBuilding = (intersections[row][col + 1] instanceof Building);
        } else if (col == intersections[0].length-1) {
            nextToBuilding = (intersections[row][col - 1] instanceof Building);
        }else nextToBuilding = (intersections[row][col - 1] instanceof Building || intersections[row][col + 1] instanceof Building);

        if(nextToBuilding) {
            return false;
        }

        //if even even or uneven uneven check below, else above if there is a building
        if((evenRow && evenCol) || (!evenRow && !evenCol)){
            if(row != intersections.length-1 && intersections[row + 1][col] instanceof Building){
                nextToBuilding = true;
            }
        } else{
            if(row != 0 && intersections[row - 1][col] instanceof Building) {
                nextToBuilding = true;
            }
        }

        return !nextToBuilding;
    }

}
