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
}
