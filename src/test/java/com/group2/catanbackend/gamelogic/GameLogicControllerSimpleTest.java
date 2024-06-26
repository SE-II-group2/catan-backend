package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NotActivePlayerException;
import com.group2.catanbackend.gamelogic.enums.ProgressCardType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.gamelogic.objects.Road;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.model.PlayerState;
import com.group2.catanbackend.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class GameLogicControllerSimpleTest {

    GameLogicController gameLogicController;
    private Player player1;
    private Player player2;
    GameMoveDto moveDto;
    private final ArrayList<Player> playersList = new ArrayList<>();
    private final int[] resourcesProgressCard = {1, 1, 0, 0, 1};

    @Mock
    MessagingService messagingService;

    @BeforeEach
    void setUp() {
        player1 = new Player("Token1", "Player One(1)", "this");
        player1.setInGameID(1);
        player1.setPlayerState(PlayerState.CONNECTED);
        player2 = new Player("Token2", "Player Two(2)", "this");
        player2.setInGameID(1);
        player2.setPlayerState(PlayerState.CONNECTED);
        playersList.add(player1);
        playersList.add(player2);
        messagingService = mock(MessagingService.class);
        gameLogicController = new GameLogicController(playersList, messagingService, "this");

    }

    @Test
    void testAddSingleSimpleVillageAndRoad(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        Board board = gameLogicController.getBoard();

        assertInstanceOf(Road.class, board.getAdjacencyMatrix()[0][1]);
        assertInstanceOf(Building.class, board.getIntersections()[0][2]);
        assertEquals(gameLogicController.getSetupPhaseTurnOrder().get(0), player2);


    }

    @Test
    void testAddTwoRoadsThrowsError(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto,player1);
        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(3);
        assertThrows(Exception.class, () -> gameLogicController.makeMove(moveDto, player2));
    }

    @Test
    void testFullSetUpPhase(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildVillageMoveDto(3);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(3);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(12);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(8);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(9);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(6);
        gameLogicController.makeMove(moveDto, player1);
        Board board = gameLogicController.getBoard();

        assertInstanceOf(Road.class, board.getAdjacencyMatrix()[0][1]);
        assertInstanceOf(Road.class, board.getAdjacencyMatrix()[3][4]);
        assertInstanceOf(Road.class, board.getAdjacencyMatrix()[4][12]);
        assertInstanceOf(Road.class, board.getAdjacencyMatrix()[0][8]);

        assertInstanceOf(Building.class, board.getIntersections()[0][2]);
        assertInstanceOf(Building.class, board.getIntersections()[0][5]);
        assertInstanceOf(Building.class, board.getIntersections()[1][6]);
        assertInstanceOf(Building.class, board.getIntersections()[1][3]);

        assertEquals(0, gameLogicController.getSetupPhaseTurnOrder().size());
        assertEquals(player1, gameLogicController.getActivePlayer());
    }

    @Test
    void testTurnOrderSetupPhaseExceptionThrow(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildRoadMoveDto(3);
        assertThrows(NotActivePlayerException.class , ()->gameLogicController.makeMove(moveDto, player1));

        moveDto = new BuildVillageMoveDto(4);
        assertThrows(NotActivePlayerException.class , ()->gameLogicController.makeMove(moveDto, player1));

        moveDto = new EndTurnMoveDto();
        assertThrows(NotActivePlayerException.class , ()->gameLogicController.makeMove(moveDto, player1));
    }

    @Test
    void testRollDiceDuringSetupPhase() {
        moveDto = new RollDiceDto(7);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(moveDto, player1));
    }

    @Test
    void testMoveRobberThrowsExceptionInSetupPhase(){
        moveDto = new MoveRobberDto(1, true);
        assertThrows(InvalidGameMoveException.class, () ->gameLogicController.makeMove(moveDto, player1));
    }

    @Test
    void testMoveRobberMovesRobber(){
        try {
            Field privateField1 = GameLogicController.class.getDeclaredField("isSetupPhase");
            privateField1.setAccessible(true); // This allows us to modify private fields
            privateField1.set(gameLogicController, false);
            gameLogicController.getBoard().setSetupPhase(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        moveDto = new MoveRobberDto(2, true);
        gameLogicController.makeMove(moveDto, player1);
        assertTrue(gameLogicController.getBoard().getHexagonList().get(2).isHasRobber());

        moveDto = new MoveRobberDto(5, true);
        gameLogicController.makeMove(moveDto, player1);
        assertTrue(gameLogicController.getBoard().getHexagonList().get(5).isHasRobber());
        assertFalse(gameLogicController.getBoard().getHexagonList().get(2).isHasRobber());
    }

    @Test
    void testMoveRobberToInvalidFieldThrowsError(){
        try {
            Field privateField1 = GameLogicController.class.getDeclaredField("isSetupPhase");
            privateField1.setAccessible(true); // This allows us to modify private fields
            privateField1.set(gameLogicController, false);
            gameLogicController.getBoard().setSetupPhase(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        moveDto = new MoveRobberDto(20, true);
        assertThrows(InvalidGameMoveException.class, () ->gameLogicController.makeMove(moveDto, player1));
        moveDto = new MoveRobberDto(-1, true);
        assertThrows(InvalidGameMoveException.class, () ->gameLogicController.makeMove(moveDto, player1));
    }

    @Test
    void testDisconnectInSetupPhaseMakesPlayerNeverToBecomeActiveAgain_playerOnTurn(){
        //no exception should be thrown.
        assertDoesNotThrow(() -> {
            player1.setPlayerState(PlayerState.DISCONNECTED);
            gameLogicController.handleDisconnect(player1);

            moveDto = new BuildVillageMoveDto(0);
            gameLogicController.makeMove(moveDto, player2);
            moveDto = new BuildRoadMoveDto(0);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new BuildVillageMoveDto(12);
            gameLogicController.makeMove(moveDto, player2);
            moveDto = new BuildRoadMoveDto(8);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new RollDiceDto(4);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new EndTurnMoveDto();
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new RollDiceDto(4);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new EndTurnMoveDto();
            gameLogicController.makeMove(moveDto, player2);
        });
    }
    @Test
    void testDisconnectInSetupPhaseMakesPlayerNeverToBecomeActiveAgain_playerNotOnTurn(){
        //no exception should be thrown.
        assertDoesNotThrow(()-> {
            ;
            moveDto = new BuildVillageMoveDto(1);
            gameLogicController.makeMove(moveDto, player1);
            moveDto = new BuildRoadMoveDto(0);

            player1.setPlayerState(PlayerState.DISCONNECTED);
            gameLogicController.handleDisconnect(player1);

            moveDto = new BuildVillageMoveDto(3);
            gameLogicController.makeMove(moveDto, player2);
            moveDto = new BuildRoadMoveDto(3);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new BuildVillageMoveDto(12);
            gameLogicController.makeMove(moveDto, player2);
            moveDto = new BuildRoadMoveDto(8);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new RollDiceDto(4);
            gameLogicController.makeMove(moveDto, player2);

            player1.setPlayerState(PlayerState.CONNECTED);

            moveDto = new EndTurnMoveDto();
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new RollDiceDto(4);
            gameLogicController.makeMove(moveDto, player2);

            moveDto = new EndTurnMoveDto();
            gameLogicController.makeMove(moveDto, player2);
        });
    }

    @Test
    void testDisconnectAfterSetupMakesPlayerSkipUntilReconnected_playerOnTurn(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildVillageMoveDto(3);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(3);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(12);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(8);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(9);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(6);
        gameLogicController.makeMove(moveDto, player1);

        assertEquals(player1, gameLogicController.getActivePlayer());

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player1);

        player1.setPlayerState(PlayerState.DISCONNECTED);
        gameLogicController.handleDisconnect(player1);

        assertEquals(player2, gameLogicController.getActivePlayer());

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        assertEquals(player2, gameLogicController.getActivePlayer());
        player1.setPlayerState(PlayerState.CONNECTED);

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        assertEquals(player1, gameLogicController.getActivePlayer());
    }

    @Test
    void testDisconnectAfterSetupMakesPlayerSkipUntilReconnected_playerNotOnTurn(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildVillageMoveDto(3);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(3);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(12);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(8);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(9);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(6);
        gameLogicController.makeMove(moveDto, player1);

        assertEquals(player1, gameLogicController.getActivePlayer());

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        assertEquals(player2, gameLogicController.getActivePlayer());

        player1.setPlayerState(PlayerState.DISCONNECTED);

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        assertEquals(player2, gameLogicController.getActivePlayer());

        player1.setPlayerState(PlayerState.CONNECTED);

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        assertEquals(player1, gameLogicController.getActivePlayer());
    }

    @Test
    void testGameOverOnceNoOneConnected(){
        player1.setPlayerState(PlayerState.DISCONNECTED);
        gameLogicController.handleDisconnect(player1);
        assertFalse(gameLogicController.isGameover());
        player2.setPlayerState(PlayerState.DISCONNECTED);
        gameLogicController.handleDisconnect(player2);
        assertTrue(gameLogicController.isGameover());
    }


    @Test
    void testYearOfPlentyCard(){
        player1.addProgressCard(ProgressCardType.YEAR_OF_PLENTY);
        player1.adjustResources(resourcesProgressCard);
        List<ResourceDistribution> chosenResources = Arrays.asList(ResourceDistribution.FIELDS, ResourceDistribution.FOREST);
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.YEAR_OF_PLENTY, chosenResources, null, 0);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(useProgressCardDto, player1);
        assertArrayEquals(new int[]{1,0,1,0,0}, player1.getResources());
    }

    @Test
    void testMonopolyCard(){
        player1.addProgressCard(ProgressCardType.MONOPOLY);
        player1.adjustResources(resourcesProgressCard);
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.MONOPOLY, null, ResourceDistribution.FIELDS, 0);
        player2.adjustResources(ResourceDistribution.FIELDS.getDistribution());
        player2.adjustResources(ResourceDistribution.FIELDS.getDistribution());
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(useProgressCardDto, player1);
        assertArrayEquals(new int[]{2,0,0,0,0}, player1.getResources());
        assertArrayEquals(new int[]{0,0,0,0,0}, player2.getResources());
    }

    @Test
    void testRoadBuildingCard(){
       player1.addProgressCard(ProgressCardType.ROAD_BUILDING);
       player1.adjustResources(resourcesProgressCard);
       UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.ROAD_BUILDING, null, null, 0);
       gameLogicController.setSetupPhase(false);
       gameLogicController.makeMove(useProgressCardDto, player1);
       assertArrayEquals(new int[]{0,0,2,2,0}, player1.getResources());
    }

    @Test
    void testVictoryPointCardWon(){
        player1.increaseVictoryPoints(9);
        player1.addProgressCard(ProgressCardType.VICTORY_POINT);
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.VICTORY_POINT, null, null, 0);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(useProgressCardDto, player1);
        int VICTORY_POINTS_FOR_VICTORY = 10;
        assertEquals(VICTORY_POINTS_FOR_VICTORY, player1.getVictoryPoints());
        assertTrue(gameLogicController.isGameover());
        verify(messagingService).notifyGameProgress(anyString(), any(GameOverDto.class));
    }
    @Test
    void testVictoryPointCardNotWon(){
        player1.addProgressCard(ProgressCardType.VICTORY_POINT);
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.VICTORY_POINT, null, null, 0);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(useProgressCardDto, player1);
        assertEquals(1, player1.getVictoryPoints());
    }
    @Test
    void testKnightCard(){
        int hexagonID = 1;
        player1.addProgressCard(ProgressCardType.KNIGHT);
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.KNIGHT, null, null, hexagonID);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(useProgressCardDto, player1);
        assertTrue(gameLogicController.getBoard().getHexagonList().get(hexagonID).isHasRobber());
    }
    @Test
    void testUseProgressCardsNotInPossession(){
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.YEAR_OF_PLENTY, null, null, 0);
        gameLogicController.setSetupPhase(false);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(useProgressCardDto, player1));
    }

    @Test
    void testUseProgressCardDuringSetupPhase() {
        player1.addProgressCard(ProgressCardType.YEAR_OF_PLENTY);
        UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.YEAR_OF_PLENTY, null, null, 0);
        gameLogicController.setSetupPhase(true);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(useProgressCardDto, player1));
    }

    @Test
    void testBuyProgressCard(){
        BuyProgressCardDto buyProgressCardDto = new BuyProgressCardDto();
        player1.adjustResources(resourcesProgressCard);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(buyProgressCardDto, player1);
        assertEquals(1, player1.getProgressCards().size());
    }

    @Test
    void testBuyProgressCardResourceNotSufficient(){
        BuyProgressCardDto buyProgressCardDto = new BuyProgressCardDto();
        gameLogicController.setSetupPhase(false);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(buyProgressCardDto, player1));
    }

    @Test
    void testResourceIndexesValidHexagons(){
        assertEquals(0, ResourceDistribution.FIELDS.getResourceIndex());
        assertEquals(1, ResourceDistribution.PASTURE.getResourceIndex());
        assertEquals(2, ResourceDistribution.FOREST.getResourceIndex());
        assertEquals(3, ResourceDistribution.HILLS.getResourceIndex());
        assertEquals(4, ResourceDistribution.MOUNTAINS.getResourceIndex());
    }

    @Test
    void testResourceIndexInvalidHexagons(){
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                ResourceDistribution.DESERT::getResourceIndex,
                "Expected getResourceIndex() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("No valid resource index found."));
    }

    @Test
    void testBuyProgressCardDuringSetupPhaseThrowsException() {
        BuyProgressCardDto buyProgressCardDto = new BuyProgressCardDto();
        gameLogicController.setSetupPhase(true);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(buyProgressCardDto, player1));
    }

    @Test
    void testMakeTradeOfferMoveWithPlayerWorking(){
        int[] offeredResources = {1,0,0,0,0};
        int[] wantedResources = {0,0,0,0,1};
        player1.adjustResources(offeredResources);
        player2.adjustResources(wantedResources);
        ArrayList<Integer> sendToPlayer = new ArrayList<>();
        sendToPlayer.add(player2.getInGameID());
        MakeTradeOfferMoveDto makeTradeOfferMoveDto = new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(makeTradeOfferMoveDto, player1);
        verify(messagingService).notifyPlayer(any(Player.class), any(TradeOfferDto.class));

    }
    @Test
    void testMakeTradeOfferMoveWithBankWorking(){
        int[] offeredResources = {4,0,0,0,0};
        int[] wantedResources = {0,0,0,0,1};
        player1.adjustResources(offeredResources);
        ArrayList<Integer> sendToPlayer = new ArrayList<>();
        MakeTradeOfferMoveDto makeTradeOfferMoveDto = new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer);
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(makeTradeOfferMoveDto, player1);
        verify(messagingService, times(2)).notifyGameProgress(anyString(), any(CurrentGameStateDto.class));
    }
    @Test
    void testMakeTradeOfferMoveGeneralFailing(){
        int[] offeredResources = {1,0,0,0,0};
        int[] wantedResources = {0,0,0,0,1};
        player1.adjustResources(offeredResources);
        ArrayList<Integer> sendToPlayer = new ArrayList<>();
        Exception exception;
        gameLogicController.setSetupPhase(true);

        exception = assertThrows(InvalidGameMoveException.class, () ->gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1));
        assertEquals(ErrorCode.ERROR_IS_SETUP_PHASE, exception.getMessage());

        gameLogicController.setSetupPhase(false);
        exception = assertThrows(NotActivePlayerException.class, () ->gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player2));
        assertEquals(ErrorCode.ERROR_NOT_ACTIVE_PLAYER.formatted(gameLogicController.getActivePlayer().getDisplayName()), exception.getMessage());

        sendToPlayer.add(player2.getInGameID());
        sendToPlayer.add(2);
        exception = assertThrows(InvalidGameMoveException.class, () ->gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1));
        assertEquals(ErrorCode.ERROR_INVALID_CONFIGURATION, exception.getMessage());
        sendToPlayer.clear();

        offeredResources[0]=2;
        exception = assertThrows(InvalidGameMoveException.class, () ->gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1));
        assertEquals(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES.formatted(gameLogicController.getActivePlayer().getDisplayName()), exception.getMessage());
    }

    @Test
    void testMakeTradeOfferMoveWithBankFailing() {
        int[] offeredResources = {3, 0, 0, 0, 0};
        int[] wantedResources = {0, 0, 0, 0, 1};
        player1.adjustResources(offeredResources);
        ArrayList<Integer> sendToPlayer = new ArrayList<>();
        gameLogicController.setSetupPhase(false);
        Exception exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1));
        assertEquals(ErrorCode.ERROR_BANK_TRADE_RATIO, exception.getMessage());
    }

    @Test
    void testAcceptTradeOfferMoveWorking(){
        int[] offeredResources = {3, 0, 0, 0, 0};
        int[] wantedResources = {0, 0, 0, 0, 1};
        player1.adjustResources(offeredResources);
        player2.adjustResources(wantedResources);
        ArrayList<Integer> sendToPlayer = new ArrayList<>();
        sendToPlayer.add(player2.getInGameID());
        gameLogicController.setSetupPhase(false);
        gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1);
        TradeOfferDto tradeOfferDto = new TradeOfferDto(negate(wantedResources), offeredResources, player1.toInGamePlayerDto());
        tradeOfferDto.setEventType(null);
        gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto), player2);
        verify(messagingService, times(2)).notifyGameProgress(anyString(), any(CurrentGameStateDto.class));
    }

    @Test
    void testAcceptTradeOfferMoveFailing(){
        final int[] offeredResources = {3, 0, 0, 0, 0};
        final int[] wantedResources = {0, 0, 0, 0, 1};
        player1.adjustResources(offeredResources);
        player2.adjustResources(wantedResources);
        ArrayList<Integer> sendToPlayer = new ArrayList<>();
        Exception exception;
        gameLogicController.setSetupPhase(false);
        sendToPlayer.add(player2.getInGameID());
        TradeOfferDto tradeOfferDto = new TradeOfferDto(negate(wantedResources), offeredResources, player1.toInGamePlayerDto());
        tradeOfferDto.setEventType(null);

        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto), player2));
        assertEquals(ErrorCode.ERROR_TRADE_NOT_AVAILABLE, exception.getMessage());

        gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1);
        gameLogicController.makeMove(new EndTurnMoveDto(), player1);
        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto), player2));
        assertEquals(ErrorCode.ERROR_TRADE_NOT_AVAILABLE, exception.getMessage());

        gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1);
        gameLogicController.setSetupPhase(true);//technically could never happen
        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto), player2));
        assertEquals(ErrorCode.ERROR_IS_SETUP_PHASE, exception.getMessage());
        gameLogicController.setSetupPhase(false);

        IngamePlayerDto ingamePlayerDto = player1.toInGamePlayerDto();
        ingamePlayerDto.setInGameID(player1.getInGameID()+1);
        final TradeOfferDto tradeOfferDto1 = new TradeOfferDto(negate(wantedResources), offeredResources, ingamePlayerDto);
        tradeOfferDto1.setEventType(null);
        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto1), player2));
        assertEquals(ErrorCode.ERROR_WRONG_TRADE, exception.getMessage());


        gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources, sendToPlayer), player1);
        final int[] offeredResources2 = new int[] {3, 1, 0, 0, 0};
        player1.adjustResources(offeredResources);
        final TradeOfferDto tradeOfferDto2 = new TradeOfferDto(negate(wantedResources), offeredResources2, player1.toInGamePlayerDto());
        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto2), player2));
        assertEquals(ErrorCode.ERROR_WRONG_TRADE, exception.getMessage());

        final int[] wantedResources2 = new int[] {0, 0, 0, 0, 2};
        final TradeOfferDto tradeOfferDto3 = new TradeOfferDto(negate(wantedResources2), offeredResources, player1.toInGamePlayerDto());
        tradeOfferDto3.setEventType(null);
        gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources), wantedResources2, sendToPlayer), player1);
        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto3), player2));
        assertEquals(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES, exception.getMessage());

        final int[] offeredResources3 = new int[] {5, 0, 0, 0, 0};
        final TradeOfferDto tradeOfferDto4 = new TradeOfferDto(negate(wantedResources), offeredResources3, player1.toInGamePlayerDto());
        tradeOfferDto4.setEventType(null);
        gameLogicController.makeMove(new MakeTradeOfferMoveDto(negate(offeredResources3), wantedResources, sendToPlayer), player1);
        player1.adjustResources(new int[]{-3, 0 ,0 ,0 ,0});
        exception = assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(new AcceptTradeOfferMoveDto(tradeOfferDto4), player2));
        assertEquals(ErrorCode.ERROR_NOT_ENOUGH_RESOURCES, exception.getMessage());

    }

    //###################################################################################################
    public int[] negate(int[] input){
        int[] result = new int[input.length];
        for (int i=0;i<input.length;i++) {
            result[i] = -input[i];
        }
        return result;
    }
}
