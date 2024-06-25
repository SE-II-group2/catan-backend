package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.*;
import com.group2.catanbackend.gamelogic.enums.ProgressCardType;
import com.group2.catanbackend.gamelogic.enums.ResourceCost;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.gamelogic.objects.Connection;
import com.group2.catanbackend.gamelogic.objects.Hexagon;
import com.group2.catanbackend.gamelogic.objects.Intersection;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.model.PlayerState;
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
    private LinkedList<Player> setupPhaseTurnOrder;
    @Getter
    private LinkedList<Player> turnOrder;
    @Getter
    private Player activePlayer;

    private boolean isSetupPhase = true;
    private static final int VICTORYPOINTSFORVICTORY = 10;
    @Getter
    private boolean gameover = false;

    private TradeOfferDto currentTrade = null;

    private Player lastCheatingPlayer = null;
    private int lastLegalRobberPlace = -1;
    private final Random random = new Random();

    public GameLogicController(@NotNull List<Player> players, @NotNull MessagingService messagingService, @NotNull String gameId) {
        this.players = players;
        this.messagingService = messagingService;
        this.gameId = gameId;
        board = new Board();
        int[] playerColors = {-65536, -16776961, -16711936, -154624}; //Red, Blue, Green, Orange
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setColor(playerColors[i]);
        }
        for( Hexagon hexagon : board.getHexagonList()){
            if(hexagon.isHasRobber()){
                lastLegalRobberPlace = hexagon.getId();
                break;
            }
        }
        initializeTurnOrder(players.size());
        //Send the starting gamestate to all playérs
        sendCurrentGameStateToPlayers();
    }

    public void setSetupPhase(boolean isSetupPhase){
        this.isSetupPhase = isSetupPhase;
    }


    public void makeMove(GameMoveDto gameMove, Player player) throws GameException {
        if (gameover) {
            throw new InvalidGameMoveException(ErrorCode.ERROR_GAME_ALREADY_OVER.formatted(players.get(0).getDisplayName()));
        }
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto" -> {
                throwIfSetupPhase();
                throwIfNotActivePlayer(player);
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
            case "BuildCityMoveDto" -> {
                BuildCityMoveDto buildCityMoveDto = (BuildCityMoveDto) gameMove;
                makeBuildCityMove(buildCityMoveDto, player);
            }
            case "EndTurnMoveDto" -> {
                throwIfNotActivePlayer(player);
                throwIfSetupPhase();
                genericNextTurn();
                lastCheatingPlayer = null;
                currentTrade = null;
                sendCurrentGameStateToPlayers();
                messagingService.notifyGameProgress(gameId, new GameProgressDto(new EndTurnMoveDto(activePlayer.toInGamePlayerDto())));
            }
            case "UseProgressCardDto" -> {
                throwIfSetupPhase();
                UseProgressCardDto useProgressCardDto = (UseProgressCardDto) gameMove;
                makeUseProgressCardMove(useProgressCardDto, player);
            }
            case "BuyProgressCardDto" -> {
                throwIfSetupPhase();
                makeBuyProgressCardMove(player);
                sendCurrentGameStateToPlayers();
            }

            case "MoveRobberDto" -> {
                if (isSetupPhase)
                    throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_MOVE_ROBBER_SETUP_PHASE);
                makeRobberMove((MoveRobberDto) gameMove, player);
            }
            case "AccuseCheatingDto" -> makeAccuseCheatingMove((AccuseCheatingDto) gameMove, player);

            case "TradeMoveDto" -> {
                TradeMoveDto tradeMove = (TradeMoveDto) gameMove;
                makeTradeMove(tradeMove, player);
            }
            case "AcceptMoveDto" -> {
                AcceptMoveDto acceptMove = (AcceptMoveDto) gameMove;
                makeAcceptMove(acceptMove, player);
            }
            default -> throw new UnsupportedGameMoveException(ErrorCode.ERROR_NOT_IMPLEMENTED);
        }
    }

    private void makeAccuseCheatingMove(AccuseCheatingDto gameMove, Player player) {
        if(lastCheatingPlayer == null) deleteHalfPlayerResources(player);
        else {
            deleteHalfPlayerResources(lastCheatingPlayer);
            board.moveRobber(lastLegalRobberPlace);
        }
        messagingService.notifyGameProgress(gameId, new GameProgressDto(gameMove));
        sendCurrentGameStateToPlayers();
    }

    private void makeRobberMove(MoveRobberDto gameMove, Player player) {
        if (gameMove.getHexagonID() < 0 || gameMove.getHexagonID() > 18)
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_MOVE_ROBBER);
        board.moveRobber(gameMove.getHexagonID());
        if(gameMove.isLegal()) {
            for (Building building : board.getHexagonList().get(gameMove.getHexagonID()).getBuildings()) {
                if (building != null && building.getPlayer() != player && stealResource(building.getPlayer(), player)) {
                    break;
                }
            }
            lastLegalRobberPlace = gameMove.getHexagonID();
        } else {
            lastCheatingPlayer = player;
        }
        sendCurrentGameStateToPlayers();
    }

    private boolean stealResource(Player playerToStealFrom, Player playerToGiveTo) {
        List<Integer> nonZeroIndices = new ArrayList<>();
        int[] opponentResources = playerToStealFrom.getResources();
        for (int i = 0; i < opponentResources.length; i++) {
            if (opponentResources[i] > 0) {
                nonZeroIndices.add(i);
            }
        }
        if (!nonZeroIndices.isEmpty()) {
            int randomIndex = nonZeroIndices.get(random.nextInt(nonZeroIndices.size()));
            int[] resourceAdjustment = new int[5];
            resourceAdjustment[randomIndex] = -1;
            playerToStealFrom.adjustResources(resourceAdjustment);

            resourceAdjustment[randomIndex] = 1;
            playerToGiveTo.adjustResources(resourceAdjustment);
            return true;
        }
        return false;
    }
    //TODO: error messages @daniel
    private void makeAcceptMove(AcceptMoveDto acceptMove, Player player){
        if(isSetupPhase)
            throw new NotActivePlayerException(ErrorCode.ERROR_IS_SETUP_PHASE);
        if(currentTrade==null)//trade is gone
            throw new InvalidGameMoveException(ErrorCode.ERROR_TRADE_NOT_AVAILABLE);
        if(activePlayer.getInGameID()!=acceptMove.getTradeOfferDto().getFromPlayer().getInGameID())
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted("active player"));
        if(!currentTrade.sameAs(acceptMove.getTradeOfferDto()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted("current trade not accepting trade"));//TradeOfferDtos are not the same
        if (!player.resourcesSufficient(acceptMove.getTradeOfferDto().getGiveResources()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted("the REAL resources sufficient"));
        if(!activePlayer.resourcesSufficient(negateAllValues(acceptMove.getTradeOfferDto().getGetResources())))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted("receiving player resources"));//trader does not have enough resources
        computeAcceptMove(acceptMove.getTradeOfferDto(), player);
    }
    private void computeAcceptMove(TradeOfferDto tradeOffer, Player player){//more
        activePlayer.adjustResources(negateAllValues(tradeOffer.getGetResources()));
        activePlayer.adjustResources(negateAllValues(tradeOffer.getGiveResources()));
        player.adjustResources(tradeOffer.getGiveResources());
        player.adjustResources(tradeOffer.getGetResources());
        this.currentTrade=null;
        sendCurrentGameStateToPlayers();
        //send new GameProgressDto? but with what content?
    }

    //TODO: rethink checks. I did not until now @daniel
    private void makeTradeMove(TradeMoveDto tradeMove, Player player){
        //if (isSetupPhase) throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_ROLL_IN_SETUP);
        if (activePlayer != player)
            throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(activePlayer.getDisplayName()));
        if(tradeMove.getToPlayers().size() > players.size()-1)
            throw new NotActivePlayerException(ErrorCode.ERROR_INVALID_CONFIGURATION);
        if (!player.resourcesSufficient(tradeMove.getGiveResources()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(tradeMove.getClass().getSimpleName()));
        if(tradeMove.getToPlayers().isEmpty()){
            computeTradeMoveBank(tradeMove, player);
        } else {
            computeTradeMove(tradeMove, player);
        }
    }

    private void computeTradeMoveBank(TradeMoveDto tradeMove, Player player){
        int countGive = -Arrays.stream(tradeMove.getGiveResources()).sum();//get positive value
        int countGet = Arrays.stream(tradeMove.getGetResources()).sum();
        if(countGive%4!=0)
            throw new InvalidGameMoveException(ErrorCode.ERROR_BANK_TRADE_RATIO);
        if(countGive/4!=countGet)
            throw new InvalidGameMoveException(ErrorCode.ERROR_BANK_TRADE_RATIO);
        // 4 to 1 trade
        player.adjustResources(tradeMove.getGiveResources());
        player.adjustResources(tradeMove.getGetResources());
        sendCurrentGameStateToPlayers();
        //send new GameProgressDto? but with what content?
    }
    private void computeTradeMove(TradeMoveDto tradeMove, Player player){
        this.currentTrade = new TradeOfferDto(negateAllValues(tradeMove.getGetResources()), negateAllValues(tradeMove.getGiveResources()), player.toInGamePlayerDto());
        for(int i = 0; i<tradeMove.getToPlayers().size(); i++){
            int playerID = tradeMove.getToPlayers().get(i);
            Player toPlayer = getPlayerByID(playerID);
            if(toPlayer!=null){
                messagingService.notifyPlayer(toPlayer, currentTrade);
            }
        }
    }
    private int[] negateAllValues(int[] input){
        int[] result = new int[input.length];
        for(int i=0;i<input.length;i++){
            result[i]=-input[i];
        }
        return result;
    }
    private Player getPlayerByID(int playerID){
        for(Player p : players){
            if(p.getInGameID()==playerID){
                return p;
            }
        }
        return null;
    }

    private void makeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        if (isSetupPhase)
            computeBuildRoadMoveSetupPhase(buildRoadMove, player);
        else
            computeBuildRoadMove(buildRoadMove, player);
    }

    private void makeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {
        if (isSetupPhase)
            computeBuildVillageMoveSetupPhase(buildVillageMove, player);
        else
            computeBuildVillageMove(buildVillageMove, player);

    }

    private void makeUseProgressCardMove(UseProgressCardDto useProgressCardDto, Player player) {
        ProgressCardType progressCardType = useProgressCardDto.getProgressCardType();
        if (!player.getProgressCards().contains(progressCardType)){
            throw new InvalidGameMoveException(ErrorCode.ERROR_CARD_TYPE_NOT_IN_POSSESSION);
        }
        player.useProgressCard(progressCardType);
        switch(progressCardType) {
            case YEAR_OF_PLENTY -> computeYearOfPlentyCardMove(useProgressCardDto, player);
            case ROAD_BUILDING -> computeRoadBuildingCardMove(player);
            case MONOPOLY -> computeMonopolyCardMove(useProgressCardDto, player);
            case VICTORY_POINT -> computeVictoryPointCardMove(player);
            case KNIGHT -> computeKnightCardMove(useProgressCardDto, player);
            default -> throw new InvalidGameMoveException("Progress Card type not acceptable");
        }
    }

    private void makeBuyProgressCardMove(Player player) {
        if (!player.resourcesSufficient(ResourceCost.DEVELOPMENT_CARD.getCost())){
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES);
        }

        ProgressCardType[] values = ProgressCardType.values();
        int randomIndex = random.nextInt(values.length);
        player.addProgressCard(values[randomIndex]);
        sendCurrentGameStateToPlayers();
    }

    private void computeYearOfPlentyCardMove(UseProgressCardDto useProgressCardDto, Player player){
        List<ResourceDistribution> chosenResources = useProgressCardDto.getChosenResources();
        player.adjustResources(chosenResources.get(0).getDistribution());
        player.adjustResources(chosenResources.get(1).getDistribution());
        sendCurrentGameStateToPlayers();
    }

    private void computeRoadBuildingCardMove(Player player) {
        for (int i = 0; i < 2; i++){
            player.adjustResources(ResourceDistribution.FOREST.getDistribution());
            player.adjustResources(ResourceDistribution.HILLS.getDistribution());
        }
        sendCurrentGameStateToPlayers();
    }

    private void computeMonopolyCardMove(UseProgressCardDto useProgressCardDto, Player player){
        ResourceDistribution monopolyResource = useProgressCardDto.getMonopolyResource();
        int resourceIndex = monopolyResource.getResourceIndex();
        int amountCollected = 0;
        for (Player otherPlayer : players) {
            if (otherPlayer != player) {
                int[] otherPlayerResources = otherPlayer.getResources();
                int amountToCollect = otherPlayerResources[resourceIndex];
                amountCollected += amountToCollect;
                int[] resourceAdjustment = new int[5];
                resourceAdjustment[resourceIndex] = -amountToCollect;
                otherPlayer.adjustResources(resourceAdjustment);
            }
        }
        int[] playerResourceAdjustment = new int[5];
        playerResourceAdjustment[resourceIndex] = amountCollected;
        player.adjustResources(playerResourceAdjustment);
        sendCurrentGameStateToPlayers();
    }

    private void computeVictoryPointCardMove(Player player){
        player.increaseVictoryPoints(1);
        if (player.getVictoryPoints() >= VICTORYPOINTSFORVICTORY) {
            gameover = true;
            messagingService.notifyGameProgress(gameId, new GameOverDto());
        }
        sendCurrentGameStateToPlayers();
    }

    private void computeKnightCardMove(UseProgressCardDto useProgressCardDto, Player player) {
        MoveRobberDto moveRobberDto = new MoveRobberDto(useProgressCardDto.getHexagonID(), true);
        makeRobberMove(moveRobberDto, player);
    }

    private void computeBuildRoadMove(BuildRoadMoveDto buildRoadMove, Player player) {
        throwIfNotActivePlayer(player);
        if (!player.resourcesSufficient(ResourceCost.ROAD.getCost()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildRoadMove.getClass().getSimpleName()));
        if (board.addNewRoad(player, buildRoadMove.getConnectionID())) {
            player.adjustResources(ResourceCost.ROAD.getCost());
            sendCurrentGameStateToPlayers();
        } else
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildRoadMove.getClass().getSimpleName()));
    }

    private void computeBuildRoadMoveSetupPhase(BuildRoadMoveDto buildRoadMove, Player player) {
        throwIfNotActivePlayer(player);
        if (!board.addNewRoad(player, buildRoadMove.getConnectionID()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildRoadMove.getClass().getSimpleName()));

        genericNextTurn();
        messagingService.notifyGameProgress(gameId, new GameProgressDto(new EndTurnMoveDto(activePlayer.toInGamePlayerDto())));
        sendCurrentGameStateToPlayers();
    }

    private void computeBuildVillageMove(BuildVillageMoveDto buildVillageMove, Player player) {
        throwIfNotActivePlayer(player);
        if (!player.resourcesSufficient(ResourceCost.VILLAGE.getCost()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildVillageMove.getClass().getSimpleName()));
        if (!board.addNewVillage(player, buildVillageMove.getIntersectionID()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildVillageMove.getClass().getSimpleName()));

        player.adjustResources(ResourceCost.VILLAGE.getCost());
        player.increaseVictoryPoints(1);
        sendCurrentGameStateToPlayers();

        if (player.getVictoryPoints() >= VICTORYPOINTSFORVICTORY) {
            gameover = true;
            messagingService.notifyGameProgress(gameId, new GameOverDto());
        }
    }

    private void makeBuildCityMove(BuildCityMoveDto buildCityMoveDto, Player player) {
        throwIfSetupPhase();
        throwIfNotActivePlayer(player);
        if (!player.resourcesSufficient(ResourceCost.CITY.getCost()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(buildCityMoveDto.getClass().getSimpleName()));
        if (!board.addNewCity(player, buildCityMoveDto.getIntersectionID()))
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildCityMoveDto.getClass().getSimpleName()));

        player.adjustResources(ResourceCost.CITY.getCost());
        player.increaseVictoryPoints(1);
        sendCurrentGameStateToPlayers();

        if (player.getVictoryPoints() >= VICTORYPOINTSFORVICTORY) {
            gameover = true;
            messagingService.notifyGameProgress(gameId, new GameOverDto());
        }
    }

    private void computeBuildVillageMoveSetupPhase(BuildVillageMoveDto buildVillageMove, Player player) {
        throwIfNotActivePlayer(player);
        if (board.addNewVillage(player, buildVillageMove.getIntersectionID())) {
            player.increaseVictoryPoints(1);
            board.distributeResourcesSetupPhase(player, buildVillageMove.getIntersectionID());
            sendCurrentGameStateToPlayers();
        } else
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_BUILD_HERE.formatted(buildVillageMove.getClass().getSimpleName()));

    }

    private void makeRollDiceMove(RollDiceDto rollDiceDto) {
        if (rollDiceDto.getDiceRoll() < 2 || rollDiceDto.getDiceRoll() > 12)
            throw new InvalidGameMoveException(ErrorCode.ERROR_INVALID_DICE_ROLL);
        if (rollDiceDto.getDiceRoll() == 7) {
            deleteHalfResourcesIfMoreThan7();
        } else board.distributeResourcesByDiceRoll(rollDiceDto.getDiceRoll());
        messagingService.notifyGameProgress(gameId, new GameProgressDto(rollDiceDto));
        sendCurrentGameStateToPlayers();
    }

    private void deleteHalfResourcesIfMoreThan7() {
        for (Player player : turnOrder) {
            int totalResources = 0;
            for (int resource : player.getResources()) {
                totalResources += resource;
            }
            if (totalResources <= 7) continue;
            deleteHalfPlayerResources(player);
        }
    }

    private void deleteHalfPlayerResources(Player player) {
        List<Integer> resourceIndexes = new ArrayList<>();
        int totalResources = 0;
        int[] resources = player.getResources();
        for (int i = 0; i < resources.length; i++) {
            for(int j = 0; j < resources[i]; j++){
                resourceIndexes.add(i);
            }
            totalResources+=resources[i];
        }
        totalResources /= 2;
        Collections.shuffle(resourceIndexes);
        int[] resourceAdjustment = new int[5];
        while (totalResources > 0) {
            int randomIndex =  resourceIndexes.remove(0);
            resourceAdjustment[randomIndex] -= 1;
            totalResources--;
        }
        player.adjustResources(resourceAdjustment);
    }

    private void sendCurrentGameStateToPlayers() {
        List<HexagonDto> hexagonDtos = getHexagonDtoList();
        List<IntersectionDto> intersectionDtos = getIntersectionDtoList();
        List<ConnectionDto> connectionDtos = getConnectionDtoList();
        List<IngamePlayerDto> playerDtos = getIngamePlayerDtoList();
        IngamePlayerDto currentPlayer = getActivePlayer().toInGamePlayerDto();
        messagingService.notifyGameProgress(gameId, new CurrentGameStateDto(hexagonDtos, intersectionDtos, connectionDtos, playerDtos, currentPlayer, isSetupPhase));
    }

    private List<IngamePlayerDto> getIngamePlayerDtoList() {
        List<IngamePlayerDto> playerDtos = new ArrayList<>();

        for (Player player : players) {
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
                    connectionDtos.add(new ConnectionDto((connection.getPlayer() == null) ? null : connection.getPlayer().toInGamePlayerDto(), board.translateIntersectionsToConnection(i, j)));
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

    private void initializeTurnOrder(int numOfPlayers) {
        setupPhaseTurnOrder = new LinkedList<>();
        turnOrder = new LinkedList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            setupPhaseTurnOrder.add(players.get(i));
            turnOrder.add(players.get(i));
        }
        for (int i = numOfPlayers - 1; i >= 0; i--) {
            setupPhaseTurnOrder.add(players.get(i));
        }
        activePlayer = setupPhaseTurnOrder.remove();
    }

    /**
     * Sets the next Player depending on whether he is connected.
     * if he is not connected, he is skipped
     */
    private void setNextPlayerTurn(){
        Player newActive = turnOrder.remove();

        int infiniteLoopGuard = 0;
        assert newActive != null;
        while(newActive.getPlayerState() != PlayerState.CONNECTED && infiniteLoopGuard < players.size()){
            turnOrder.add(newActive);
            newActive = turnOrder.remove();
            infiniteLoopGuard++;
        }
        activePlayer = newActive;
        turnOrder.add(newActive);
    }

    private void genericNextTurn(){
        if(isSetupPhase){
            if(setupPhaseTurnOrder.isEmpty()){
                board.setSetupPhase(false);
                isSetupPhase = false;
                setNextPlayerTurn();
            }else {
                activePlayer = setupPhaseTurnOrder.remove();
            }
        }else {
            setNextPlayerTurn();
        }
    }

    private boolean atLeastOneConnected(){
        for(Player p : players){
            if(p.getPlayerState() == PlayerState.CONNECTED)
                return true;
        }
        return false;
    }

    public void handleDisconnect(Player p){
        if(!atLeastOneConnected()){
            gameover = true;
            messagingService.notifyGameProgress(gameId, new GameOverDto());
            return;
        }

        if(!isSetupPhase && activePlayer == p){
            setNextPlayerTurn();
            lastCheatingPlayer = null;
        }
        if(isSetupPhase){
            turnOrder.remove(p); //will never become active again.
            boolean forceNextPlayer = activePlayer == p;
            while(true){
                if(!setupPhaseTurnOrder.remove(p)) break;
            }
            if(forceNextPlayer) {
                lastCheatingPlayer = null;
                currentTrade = null;
                genericNextTurn();
            }
        }
        sendCurrentGameStateToPlayers();
        messagingService.notifyGameProgress(gameId, new GameProgressDto(new EndTurnMoveDto(activePlayer.toInGamePlayerDto())));
    }

    public void handleReconnect(){
        sendCurrentGameStateToPlayers();
    }

    private void throwIfSetupPhase() throws InvalidGameMoveException {
        if(isSetupPhase)
            throw new InvalidGameMoveException(ErrorCode.ERROR_CANT_ROLL_IN_SETUP);
    }

    private void throwIfNotActivePlayer(Player player) throws NotActivePlayerException {
        if(activePlayer != player)
            throw new NotActivePlayerException(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(activePlayer.getDisplayName()));
    }

}
