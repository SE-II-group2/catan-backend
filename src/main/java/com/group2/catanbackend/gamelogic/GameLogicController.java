package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NotActivePlayerException;
import com.group2.catanbackend.exception.UnsupportedGameMoveException;
import com.group2.catanbackend.gamelogic.enums.ResourceCost;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameLogicController {
    @Getter
    private Board board;
    @Getter
    private List<Player> players;
    private MessagingService messagingService;
    @Getter
    private String gameId;
    private ArrayList<String> setupPhaseTurnOrder;
    private ArrayList<Player> turnOrder;
    private boolean isSetupPhase = true;

    public GameLogicController(List<Player> players, MessagingService runningInstanceService, String gameId) {
        this.players = players;
        this.messagingService = runningInstanceService;
        this.gameId = gameId;

        board = new Board();
        generateSetupPhaseTurnOrder(players.size());
    }

    public void makeMove(GameMoveDto gameMove, Player player) throws GameException {
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto" -> {
                if (isSetupPhase) throw new InvalidGameMoveException("cant roll dice during setupPhase");
                if(turnOrder.get(0)!=player)throw new NotActivePlayerException("Not the active player right now");
                RollDiceDto rollDiceMove = (RollDiceDto) gameMove;
                makeRollDiceMove(rollDiceMove);
            }
            case "BuildRoadMoveDto" -> {
                BuildRoadMoveDto buildRoadMove = (BuildRoadMoveDto) gameMove;
                makeBuildRoadMove(buildRoadMove, player);
            }
            case "BuildVillageMoveDto" -> {
                BuildVillageMoveDto buildVillageMove = (BuildVillageMoveDto) gameMove;
                makeBuildVillageMove(buildVillageMove, player);
            }
            case "EndTurnMoveDto" ->{
                if(isSetupPhase)throw new InvalidGameMoveException("the Turn is automatically passed after setting down your road, no need to send this");
                if(turnOrder.get(0)!=player)throw new NotActivePlayerException("Not the active player right now");
                turnOrder.remove(0);
                turnOrder.add(player);
            }
            default -> throw new UnsupportedGameMoveException("Not a valid Dto Format");
        }
    }

    private void makeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        if (isSetupPhase) {
            if (!setupPhaseTurnOrder.get(0).equalsIgnoreCase(player.getToken()))
                throw new NotActivePlayerException("Not the active player right now");
            if (board.addNewRoad(player, buildRoadMove.getFromIntersection(), buildRoadMove.getToIntersection())) {
                setupPhaseTurnOrder.remove(0); //after you set down your road your turn ends during the setup phase
                if (setupPhaseTurnOrder.size() == 0) {
                    isSetupPhase = false;
                    board.setSetupPhase(false);
                }
            }
            return;
        }
        if(turnOrder.get(0)!=player)throw new NotActivePlayerException("Not the active player right now");
        if(player.resourcesSufficient(ResourceCost.ROAD.getCost())){
            if(board.addNewRoad(player, buildRoadMove.getFromIntersection(), buildRoadMove.getToIntersection())){
                player.adjustResources(ResourceCost.ROAD.getCost());
            }
        }
    }

    private void makeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {
        if (isSetupPhase) {
            if (!setupPhaseTurnOrder.get(0).equalsIgnoreCase(player.getToken()))
                throw new NotActivePlayerException("Not the active player right now");
            if (!board.addNewVillage(player, buildVillageMove.getRow(), buildVillageMove.getCol())) {
                throw new InvalidGameMoveException("cant build a Village here!");
            }
        }
        if(turnOrder.get(0)!=player)throw new NotActivePlayerException("Not the active player right now");
        if(player.resourcesSufficient(ResourceCost.VILLAGE.getCost())){
            if(board.addNewVillage(player, buildVillageMove.getCol(), buildVillageMove.getRow())){
                player.adjustResources(ResourceCost.ROAD.getCost());
            }
        }
    }

    private void makeRollDiceMove(RollDiceDto rollDiceDto) {
        board.distributeResourcesByDiceRoll(rollDiceDto.getDiceRoll());
    }

    private void generateSetupPhaseTurnOrder(int numOfPlayers) {
        ArrayList<String> setupPhaseTurnOrder = new ArrayList<>();
        ArrayList<Player> turnOrder = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            setupPhaseTurnOrder.add(players.get(i).getToken());
            turnOrder.add(players.get(i));
        }
        for (int i = numOfPlayers - 2; i >= 0; i--) {
            setupPhaseTurnOrder.add(players.get(i).getToken());
        }
    }
}
