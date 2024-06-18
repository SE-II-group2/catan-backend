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
        Connection[][] adjacencyMatrix = board.getAdjacencyMatrix();

        //check the specific intersection in the adjacencyMatrix if there are any roads, and if it belongs to the playerID who wants to build
        for(int i = 0; i < 54; i++){
            if((adjacencyMatrix[i][intersectionID] instanceof Road) && (adjacencyMatrix[i][intersectionID].getPlayer() == player)){
                return true;
            }
        }
        return false;
    }

    public boolean notNextToBuilding(Board board, int row, int col) {
        Intersection[][] intersections = board.getIntersections();

        return !isNextToHorizontalBuilding(intersections, row, col) && !isNextToVerticalBuilding(intersections, row, col);
    }

    private boolean isNextToHorizontalBuilding(Intersection[][] intersections, int row, int col) {
        if (col > 0 && intersections[row][col - 1] instanceof Building) {
            return true;
        }
        return col < intersections[0].length - 1 && intersections[row][col + 1] instanceof Building;
    }

    private boolean isNextToVerticalBuilding(Intersection[][] intersections, int row, int col) {
        boolean evenCol = col % 2 == 0;
        boolean evenRow = row % 2 == 0;

        if ((evenRow && evenCol) || (!evenRow && !evenCol)) {
            return row < intersections.length - 1 && intersections[row + 1][col] instanceof Building;
        } else {
            return row > 0 && intersections[row - 1][col] instanceof Building;
        }
    }


}
