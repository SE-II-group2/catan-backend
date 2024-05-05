package com.group2.catanbackend.gamelogic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.*;
import com.group2.catanbackend.gamelogic.enums.ResourceCost;
import com.group2.catanbackend.gamelogic.objects.Connection;
import com.group2.catanbackend.gamelogic.objects.Hexagon;
import com.group2.catanbackend.gamelogic.objects.Intersection;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameLogicController {
    @Getter
    private final Board board;
    private final List<Player> players;
    private final MessagingService messagingService;
    @Getter
    private final String gameId;
    @Getter
    private ArrayList<Player> setupPhaseTurnOrder;
    @Getter
    private ArrayList<Player> turnOrder;
    private boolean isSetupPhase = true;
    private static final int VICTORYPOINTSFORVICTORY = 10;
    @Getter
    private boolean gameover = false;

    public GameLogicController(@NotNull List<Player> players, @NotNull MessagingService messagingService, @NotNull String gameId) {
        this.players = players;
        this.messagingService = messagingService;
        this.gameId = gameId;
        board = new Board();
        generateSetupPhaseTurnOrder(players.size());
        sendCurrentGameStateToPlayers();
    }



    public void makeMove(GameMoveDto gameMove, Player player) throws GameException {
        if(gameover){
            throw new InvalidGameMoveException(ErrorCode.ERROR_GAME_ALREADY_OVER.formatted(players.get(0).getDisplayName()));
        }
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto" -> {
                if (isSetupPhase) throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_ROLL_IN_SETUP);
                if (turnOrder.get(0) != player) throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));
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
                if (isSetupPhase)throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));
                if (turnOrder.get(0) != player) throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));
                turnOrder.remove(0);
                turnOrder.add(player);
                messagingService.notifyGameProgress(gameId, new GameProgressDto(gameMove, player.toPlayerDto()));
            }
            //TODO To implement other moves create MoveDto and include it here
            default -> throw new UnsupportedGameMoveException("Unknown DTO Format");
        }
    }

    private void makeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        if (isSetupPhase) {
            if (!(setupPhaseTurnOrder.get(0) == player))
                throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

            if (board.addNewRoad(player, buildRoadMove.getConnectionID())) {
                setupPhaseTurnOrder.remove(0); //after you set down your road your turn ends during the setup phase
                if (setupPhaseTurnOrder.isEmpty()) {
                    isSetupPhase = false;
                    board.setSetupPhase(false);
                }
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildRoadMove, player.toPlayerDto()));

            } else throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildRoadMove.getClass().getSimpleName()));
            return;
        }

        if (turnOrder.get(0) != player) throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

        if (player.resourcesSufficient(ResourceCost.ROAD.getCost())) {
            if (board.addNewRoad(player, buildRoadMove.getConnectionID())) {
                player.adjustResources(ResourceCost.ROAD.getCost());
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildRoadMove, player.toPlayerDto()));
            } else throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildRoadMove.getClass().getSimpleName()));
        } else throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildRoadMove.getClass().getSimpleName()));
    }

    private void makeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {
        if (isSetupPhase) {
            if (!(setupPhaseTurnOrder.get(0) == player))
                throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

            if (board.addNewVillage(player, buildVillageMove.getIntersection())) {
                player.increaseVictoryPoints(1);
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildVillageMove, player.toPlayerDto()));
            } else throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildVillageMove.getClass().getSimpleName()));
            return;
        }

        if (turnOrder.get(0) != player) throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

        if (player.resourcesSufficient(ResourceCost.VILLAGE.getCost())) {
            if (board.addNewVillage(player, buildVillageMove.getIntersection())) {
                player.adjustResources(ResourceCost.VILLAGE.getCost());
                player.increaseVictoryPoints(1);
                messagingService.notifyGameProgress(gameId, new GameProgressDto(buildVillageMove, player.toPlayerDto()));

                if(player.getVictoryPoints() >= VICTORYPOINTSFORVICTORY){
                    gameover=true;
                    messagingService.notifyGameProgress(gameId, new GameoverDto(player.toPlayerDto()));
                }
            }
            else {
                throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildVillageMove.getClass().getSimpleName()));
            }
        }
        else throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildVillageMove.getClass().getSimpleName()));
    }

    private void makeRollDiceMove(RollDiceDto rollDiceDto, Player player) {
        if(rollDiceDto.getDiceRoll()<2 || rollDiceDto.getDiceRoll()>12) throw new InvalidGameMoveException(ErrorCode.ERROR_INVALID_DICE_ROLL);
        board.distributeResourcesByDiceRoll(rollDiceDto.getDiceRoll());
        messagingService.notifyGameProgress(gameId, new GameProgressDto(rollDiceDto, player.toPlayerDto()));
    }

    private void sendCurrentGameStateToPlayers() {
        List<HexagonDto> hexagonDtos = new ArrayList<>();
        for(Hexagon hexagon : board.getHexagonList()){
            hexagonDtos.add(new HexagonDto(hexagon.getLocation(), hexagon.getDistribution(), hexagon.getRollValue(), hexagon.getId()));
        }
        List<IntersectionDto> intersectionDtos = new ArrayList<>();
        int id = 0;
        for (Intersection[] intersectionRow : board.getIntersections()) {
            for (Intersection intersection : intersectionRow) {
                if (intersection != null) {
                    intersectionDtos.add(new IntersectionDto((intersection.getPlayer() == null) ? null : intersection.getPlayer().toPlayerDto(), intersection.getType().name(), id++));
                }
            }
        }
        List<ConnectionDto> connectionDtos = new ArrayList<>();
        id = 0;
        for (Connection[] connectionRow : board.getAdjacencyMatrix()) {
            for (Connection connection : connectionRow) {
                if (connection != null) {
                    connectionDtos.add(new ConnectionDto((connection.getPlayer() == null) ? null : connection.getPlayer().toPlayerDto(), id++));
                }
            }
        }
        messagingService.notifyGameProgress(gameId, new CurrentGameStateDto(hexagonDtos, intersectionDtos, connectionDtos));
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
