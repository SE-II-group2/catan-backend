package com.group2.catanbackend.gamelogic;

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

import java.util.*;

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

    private int[] playerColors = {-65536, -16776961, -16711936, -1}; //Red, Blue, Green, White

    public GameLogicController(@NotNull List<Player> players, @NotNull MessagingService messagingService, @NotNull String gameId) {
        this.players = players;
        this.messagingService = messagingService;
        this.gameId = gameId;
        board = new Board();
        for (int i = 0; i<players.size(); i++) {
            players.get(i).setColor(playerColors[i]);
        }
        generateSetupPhaseTurnOrder(players.size());
        //Send the starting gamestate to all playérs
        sendCurrentGameStateToPlayers();
    }


    public void makeMove(GameMoveDto gameMove, Player player) throws GameException {
        if (gameover) {
            throw new InvalidGameMoveException(ErrorCode.ERROR_GAME_ALREADY_OVER.formatted(players.get(0).getDisplayName()));
        }
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto" -> {
                if (isSetupPhase) throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_ROLL_IN_SETUP);
                if (turnOrder.get(0) != player)
                    throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));
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
            case "EndTurnMoveDto" -> {
                if (isSetupPhase)
                    throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));
                if (turnOrder.get(0) != player)
                    throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));
                turnOrder.remove(0);
                turnOrder.add(player);
                sendCurrentGameStateToPlayers();
                messagingService.notifyGameProgress(gameId, new GameProgressDto(new EndTurnMoveDto((isSetupPhase) ? setupPhaseTurnOrder.get(0).toInGamePlayerDto() : turnOrder.get(0).toInGamePlayerDto())));
            }
            //TODO To implement other moves create MoveDto and include it here
            default -> throw new UnsupportedGameMoveException("Unknown DTO Format");
        }
    }

    private void makeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        if (isSetupPhase) {
            computeBuildRoadMoveSetupPhase(buildRoadMove, player);
        }
        else computeBuildRoadMove(buildRoadMove, player);
    }

    private void makeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {
        if (isSetupPhase) {
            computeBuildVillageMoveSetupPhase(buildVillageMove, player);
        }
        else computeBuildVillageMove(buildVillageMove, player);

    }


    private void computeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        if (turnOrder.get(0) != player)
            throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

        if (!player.resourcesSufficient(ResourceCost.ROAD.getCost()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildRoadMove.getClass().getSimpleName()));
        if (board.addNewRoad(player, buildRoadMove.getConnectionID())) {
            player.adjustResources(ResourceCost.ROAD.getCost());
            sendCurrentGameStateToPlayers();
        } else
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildRoadMove.getClass().getSimpleName()));
    }

    private void computeBuildRoadMoveSetupPhase(BuildRoadMoveDto buildRoadMove, Player player) {
        if (!(setupPhaseTurnOrder.get(0) == player))
            throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

        if (!board.addNewRoad(player, buildRoadMove.getConnectionID()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildRoadMove.getClass().getSimpleName()));
        setupPhaseTurnOrder.remove(0); //after you set down your road your turn ends during the setup phase
        if (setupPhaseTurnOrder.isEmpty()) {
            isSetupPhase = false;
            board.setSetupPhase(false);
            messagingService.notifyGameProgress(gameId, new GameProgressDto(new EndTurnMoveDto(turnOrder.get(0).toInGamePlayerDto())));
        } else
            messagingService.notifyGameProgress(gameId, new GameProgressDto(new EndTurnMoveDto(setupPhaseTurnOrder.get(0).toInGamePlayerDto())));
        sendCurrentGameStateToPlayers();
    }

    private void computeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {

        if (turnOrder.get(0) != player)
            throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

        if (player.resourcesSufficient(ResourceCost.VILLAGE.getCost())) {
            if (board.addNewVillage(player, buildVillageMove.getIntersectionID())) {
                player.adjustResources(ResourceCost.VILLAGE.getCost());
                player.increaseVictoryPoints(1);
                sendCurrentGameStateToPlayers();

                if (player.getVictoryPoints() >= VICTORYPOINTSFORVICTORY) {
                    gameover = true;
                    messagingService.notifyGameProgress(gameId, new GameoverDto(player.toInGamePlayerDto()));
                }
            } else {
                throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildVillageMove.getClass().getSimpleName()));
            }
        } else
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildVillageMove.getClass().getSimpleName()));
    }

    private void computeBuildVillageMoveSetupPhase(BuildVillageMoveDto buildVillageMove, Player player) {
        if (!(setupPhaseTurnOrder.get(0) == player))
            throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(players.get(0).getDisplayName()));

        if (board.addNewVillage(player, buildVillageMove.getIntersectionID())) {
            player.increaseVictoryPoints(1);
            sendCurrentGameStateToPlayers();
        } else
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildVillageMove.getClass().getSimpleName()));

    }

    private void makeRollDiceMove(RollDiceDto rollDiceDto) {
        if (rollDiceDto.getDiceRoll() < 2 || rollDiceDto.getDiceRoll() > 12)
            throw new InvalidGameMoveException(ErrorCode.ERROR_INVALID_DICE_ROLL);
        board.distributeResourcesByDiceRoll(rollDiceDto.getDiceRoll());
        messagingService.notifyGameProgress(gameId, new GameProgressDto(rollDiceDto));
        sendCurrentGameStateToPlayers();
    }

    private void sendCurrentGameStateToPlayers() {
        List<HexagonDto> hexagonDtos = getHexagonDtoList();
        List<IntersectionDto> intersectionDtos = getIntersectionDtoList();
        List<ConnectionDto> connectionDtos = getConnectionDtoList();
        List<IngamePlayerDto> playerDtos = getIngamePlayerDtoList();
        messagingService.notifyGameProgress(gameId, new CurrentGameStateDto(hexagonDtos, intersectionDtos, connectionDtos, playerDtos, isSetupPhase));
    }

    private List<IngamePlayerDto> getIngamePlayerDtoList() {
        List<IngamePlayerDto> playerDtos = new ArrayList<>();

        for (Player player : (isSetupPhase) ? setupPhaseTurnOrder : turnOrder) {
            playerDtos.add(player.toInGamePlayerDto());
        }
    return playerDtos;
    }

    private List<ConnectionDto> getConnectionDtoList() {
        List<ConnectionDto> connectionDtos = new ArrayList<>();
        Map<String, Boolean> visitedConnections = new HashMap<>();
        
        for (int i = 0; i < board.getAdjacencyMatrix().length; i++) {
            for (int j = i + 1; j < board.getAdjacencyMatrix()[i].length; j++) {
                Connection connection = board.getAdjacencyMatrix()[i][j];
                if (connection != null && !visitedConnections.containsKey(i + "-" + j)) {
                    connectionDtos.add(new ConnectionDto((connection.getPlayer() == null) ? null : connection.getPlayer().toInGamePlayerDto(), board.getConnectionIdFromIntersections(i, j)));
                    visitedConnections.put(i + "-" + j, true);
                    visitedConnections.put(j + "-" + i, true);  // Mark both [i][j] and [j][i] as visited
                }
            }
        }
        Comparator<ConnectionDto> connectionDtoComparator = Comparator.comparingInt(ConnectionDto::getId);
        connectionDtos.sort(connectionDtoComparator);
        
        return connectionDtos;
    }

    private List<IntersectionDto> getIntersectionDtoList() {
        List<IntersectionDto> intersectionDtos = new ArrayList<>();
        int id = 0;
        for (Intersection[] intersectionRow : board.getIntersections()) {
            for (Intersection intersection : intersectionRow) {
                if (intersection != null) {
                    intersectionDtos.add(new IntersectionDto((intersection.getPlayer() == null) ? null : intersection.getPlayer().toInGamePlayerDto(), intersection.getType().name(), id++));
                }
            }
        }
        return intersectionDtos;
    }

    private List<HexagonDto> getHexagonDtoList() {
        List<HexagonDto> hexagonDtos = new ArrayList<>();
        for (Hexagon hexagon : board.getHexagonList()) {
            hexagonDtos.add(new HexagonDto(hexagon.getHexagonType(), hexagon.getDistribution(), hexagon.getRollValue(), hexagon.getId(), hexagon.isHasRobber()));
        }
        return hexagonDtos;
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
