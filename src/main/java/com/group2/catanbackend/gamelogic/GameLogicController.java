package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NotActivePlayerException;
import com.group2.catanbackend.exception.UnsupportedGameMoveException;
import com.group2.catanbackend.gamelogic.enums.ResourceCost;
import com.group2.catanbackend.gamelogic.objects.Hexagon;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameLogicController {
    @Getter
    private final Board board;
    @Getter
    private final List<Player> players;
    private final MessagingService messagingService;
    @Getter
    private final String gameId;
    @Getter
    private ArrayList<Player> setupPhaseTurnOrder;
    @Getter
    private ArrayList<Player> turnOrder;
    private boolean isSetupPhase = true;

    public GameLogicController(@NotNull List<Player> players, @NotNull MessagingService messagingService, @NotNull String gameId) {
        this.players = players;
        this.messagingService = messagingService;
        this.gameId = gameId;
        board = new Board();
        generateSetupPhaseTurnOrder(players.size());

        List<HexagonDto> hexagonDtos = new ArrayList<>();
        for(Hexagon hexagon : board.getHexagonList()){
            hexagonDtos.add(new HexagonDto(hexagon.getLocation(), hexagon.getDistribution(), hexagon.getRollValue(), hexagon.getId()));
        }
        messagingService.notifyLobby(gameId, new HexagonListDto(hexagonDtos));
    }

    public void makeMove(GameMoveDto gameMove, Player player) throws GameException {
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto" -> {
                if (isSetupPhase) throw new InvalidGameMoveException("cant roll dice during setupPhase");
                if (turnOrder.get(0) != player) throw new NotActivePlayerException("Not the active player right now");
                RollDiceDto rollDiceMove = (RollDiceDto) gameMove;
                makeRollDiceMove(rollDiceMove, player);
            }
            case "BuildRoadMoveDto" -> {
                BuildRoadMoveDto buildRoadMove = (BuildRoadMoveDto) gameMove;
                makeBuildRoadMove(buildRoadMove, player);
            }
            case "BuildVillageMoveDto" -> {
                BuildVillageMoveDto buildVillageMove = (BuildVillageMoveDto) gameMove;
                makeBuildVillageMove(buildVillageMove, player);
            }
            case "EndTurnMoveDto" -> {
                if (isSetupPhase)
                    throw new InvalidGameMoveException("the Turn is automatically passed after setting down your road, no need to send this");
                if (turnOrder.get(0) != player) throw new NotActivePlayerException("Not the active player right now");
                turnOrder.remove(0);
                turnOrder.add(player);
                messagingService.notifyGameProgress(gameId, new GameProgressDto(gameMove, player.toPlayerDto()));
            }
            default -> throw new UnsupportedGameMoveException("Unknown DTO Format");
        }
    }

    private void makeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        if (isSetupPhase) {
            if (!(setupPhaseTurnOrder.get(0) == player))
                throw new NotActivePlayerException("Not the active player right now");
            if (board.addNewRoad(player, buildRoadMove.getFromIntersection(), buildRoadMove.getToIntersection())) {
                setupPhaseTurnOrder.remove(0); //after you set down your road your turn ends during the setup phase
                if (setupPhaseTurnOrder.size() == 0) {
                    isSetupPhase = false;
                    board.setSetupPhase(false);
                }
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildRoadMove, player.toPlayerDto()));
            } else throw new InvalidGameMoveException("Not a valid place to build a Road!");
            return;
        }
        if (turnOrder.get(0) != player) throw new NotActivePlayerException("Not the active player right now");
        if (player.resourcesSufficient(ResourceCost.ROAD.getCost())) {
            if (board.addNewRoad(player, buildRoadMove.getFromIntersection(), buildRoadMove.getToIntersection())) {
                player.adjustResources(ResourceCost.ROAD.getCost());
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildRoadMove, player.toPlayerDto()));
            } else throw new InvalidGameMoveException("Not a valid place to build a Road!");
        } else throw new InvalidGameMoveException("Not enough Resources!");
    }

    private void makeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {
        if (isSetupPhase) {
            if (!(setupPhaseTurnOrder.get(0) == player))
                throw new NotActivePlayerException("Not the active player right now");
            if (board.addNewVillage(player, buildVillageMove.getRow(), buildVillageMove.getCol())) {
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildVillageMove, player.toPlayerDto()));
            } else throw new InvalidGameMoveException("Cant build a Village here!");

            return;
        }
        if (turnOrder.get(0) != player) throw new NotActivePlayerException("Not the active player right now");
        if (player.resourcesSufficient(ResourceCost.VILLAGE.getCost())) {
            if (board.addNewVillage(player, buildVillageMove.getRow(), buildVillageMove.getCol())) {
                player.adjustResources(ResourceCost.VILLAGE.getCost());
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildVillageMove, player.toPlayerDto()));
            }
            else {
                throw new InvalidGameMoveException("Cant build a Village here!");
            }
        }
        else throw new InvalidGameMoveException("Not enough Resources!");
    }

    private void makeRollDiceMove(RollDiceDto rollDiceDto, Player player) {
        board.distributeResourcesByDiceRoll(rollDiceDto.getDiceRoll());
        messagingService.notifyGameProgress(gameId, new GameProgressDto(rollDiceDto, player.toPlayerDto()));
    }

    private void generateSetupPhaseTurnOrder(int numOfPlayers) {
        setupPhaseTurnOrder = new ArrayList<>();
        turnOrder = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            setupPhaseTurnOrder.add(players.get(i));
            turnOrder.add(players.get(i));
        }
        for (int i = numOfPlayers - 1; i >= 0; i--) {
            setupPhaseTurnOrder.add(players.get(i));
        }
    }
}
