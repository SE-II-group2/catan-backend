package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.enums.HexagonType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.gamelogic.objects.Hexagon;
import com.group2.catanbackend.gamelogic.objects.Road;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameLogicControllerExpandedTest {

    GameLogicController gameLogicController;
    private Player player1;
    private Player player2;
    GameMoveDto moveDto;
    private final ArrayList<Player> playersList = new ArrayList<>();
    private ArgumentCaptor<MessageDto> argumentCaptor;
    @Mock
    MessagingService messagingMock;


    @BeforeEach
    public void setUp() {
        player1 = new Player("Token1", "Player One(1)", "this");
        player1.setInGameID(1);
        player2 = new Player("Token2", "Player Two(2)", "this");
        player2.setInGameID(1);
        playersList.add(player1);
        playersList.add(player2);
        messagingMock = mock(MessagingService.class);
        gameLogicController = new GameLogicController(playersList, messagingMock, "this");
        verify(messagingMock, times(1)).notifyGameProgress(any(), any());
        createPreSetupBoard();
        finishSetUpPhase();

        argumentCaptor = ArgumentCaptor.forClass(MessageDto.class);

    }

    @Test
    public void testResourceDistribution() {
        moveDto = new RollDiceDto(2);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new RollDiceDto(6);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        assertArrayEquals(new int[]{1, 2, 2, 4, 2}, player1.getResources());
        assertArrayEquals(new int[]{1, 1, 3, 2, 0}, player2.getResources());
    }

    @Test
    public void testInvalidBuildVillageMove() {
        moveDto = new BuildVillageMoveDto(21);
        assertThrows(GameException.class, () -> gameLogicController.makeMove(moveDto, player1));

        try {
            Field privateField = Player.class.getDeclaredField("resources");
            privateField.setAccessible(true); // This allows us to modify private fields
            privateField.set(player1, new int[]{5, 5, 5, 5, 5});
        } catch (Exception e) {
            e.printStackTrace();
        }

        moveDto = new BuildVillageMoveDto(9);
        assertThrows(GameException.class, () -> gameLogicController.makeMove(moveDto, player1));
    }

    @Test
    public void testCommunicationFromServerOnValidMoves() {
        moveDto = new RollDiceDto(2);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        verify(messagingMock, atLeastOnce()).notifyGameProgress(eq(gameLogicController.getGameId()), argumentCaptor.capture());
        List<MessageDto> allValues = argumentCaptor.getAllValues();
        CurrentGameStateDto argument = null;
        for (int i = allValues.size() - 1; i >= 0; i--) {
            if (allValues.get(i).getEventType().equalsIgnoreCase("GAME_OBJECT")) {
                argument = (CurrentGameStateDto) allValues.get(i);
                break;
            }
        }
        if (argument == null) fail("argument was null, no currentgamestate sent to players");
        assertEquals(BuildingType.VILLAGE.name(), argument.getIntersections().get(29).getBuildingType());
        assertEquals(player1.getDisplayName(), argument.getIntersections().get(29).getOwner().getDisplayName());

        assertNotNull(argument.getConnections().get(36).getOwner());
        assertEquals(player1.getDisplayName(), argument.getConnections().get(36).getOwner().getDisplayName());

        assertArrayEquals(new int[]{1, 2, 1, 3, 2}, argument.getPlayerOrder().get(0).getResources());
    }

    @Test
    public void testVictoryCondition() {
        try {
            Field privateFieldVictoryPoints = Player.class.getDeclaredField("victoryPoints");
            privateFieldVictoryPoints.setAccessible(true); // This allows us to modify private fields
            privateFieldVictoryPoints.set(player1, 9);

            Field privateFieldResources = Player.class.getDeclaredField("resources");
            privateFieldResources.setAccessible(true); // This allows us to modify private fields
            privateFieldResources.set(player1, new int[]{5, 5, 5, 5, 5});

            moveDto = new BuildRoadMoveDto(13);
            gameLogicController.makeMove(moveDto, player1);

            moveDto = new BuildVillageMoveDto(11);
            gameLogicController.makeMove(moveDto, player1);


            assertTrue(gameLogicController.isGameover());
            verify(messagingMock, atLeastOnce()).notifyGameProgress(eq(gameLogicController.getGameId()), argumentCaptor.capture());

            List<MessageDto> allValues = argumentCaptor.getAllValues();
            GameoverDto lastArgument = (GameoverDto) allValues.get(allValues.size() - 1); //get the last Dto sent, should always be gameoverdto
            assertEquals(lastArgument.getWinner().getDisplayName(), player1.getDisplayName());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testValidBuildingOutOfSetupPhase() {
        moveDto = new RollDiceDto(2);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new RollDiceDto(4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new RollDiceDto(6);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildRoadMoveDto(13);
        gameLogicController.makeMove(moveDto, player1);

        assertTrue(gameLogicController.getBoard().getAdjacencyMatrix()[10][11] instanceof Road);
        assertArrayEquals(new int[]{1, 2, 1, 3, 2}, player1.getResources());

        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new RollDiceDto(6);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new RollDiceDto(9);
        gameLogicController.makeMove(moveDto, player1);

        assertArrayEquals(new int[]{2, 2, 2, 4, 2}, player1.getResources());
        assertArrayEquals(new int[]{1, 1, 3, 2, 0}, player2.getResources());

        moveDto = new BuildVillageMoveDto(11);
        gameLogicController.makeMove(moveDto, player1);

        player1.adjustResources(new int[]{5,5,5,5,5});
        moveDto = new BuildCityMoveDto(11);
        gameLogicController.makeMove(moveDto, player1);

        assertInstanceOf(Building.class, gameLogicController.getBoard().getIntersections()[1][5]);
        assertArrayEquals(new int[]{4, 6, 6, 8, 4}, player1.getResources());
    }

    @Test
    public void testInvalidCityDuringSetupPhase() {
        player1.adjustResources(new int[]{5,5,5,5,5});
        moveDto = new BuildCityMoveDto(0);
        assertThrows(GameException.class, () -> gameLogicController.makeMove(moveDto, player1));
    }
    @Test
    public void testRobberMoveNoResourcesToSteal() {
        try {
            Field privateField = Player.class.getDeclaredField("resources");
            privateField.setAccessible(true); // This allows us to modify private fields
            privateField.set(player1, new int[]{0, 0, 0, 0, 0});
            privateField.set(player2, new int[]{0, 0, 0, 0, 0});
        } catch (Exception e) {
            e.printStackTrace();
        }
        MoveRobberDto moveRobberDto = new MoveRobberDto(2, true);
        gameLogicController.makeMove(moveRobberDto, player1);

        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, player2.getResources());
        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, player1.getResources());
    }

    @Test
    public void testRobberMoveWithResourcesToSteal() {
        try {
            Field privateField = Player.class.getDeclaredField("resources");
            privateField.setAccessible(true); // This allows us to modify private fields
            privateField.set(player1, new int[]{0, 0, 0, 0, 0});
            privateField.set(player2, new int[]{1, 0, 0, 0, 0});
        } catch (Exception e) {
            e.printStackTrace();
        }

        MoveRobberDto moveRobberDto = new MoveRobberDto(2, true);
        gameLogicController.makeMove(moveRobberDto, player1);

        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, player2.getResources());
        assertArrayEquals(new int[]{1, 0, 0, 0, 0}, player1.getResources());
    }

    @Test
    public void testRobberMovePlayerIsSame() {
        try {
            Field privateField = Player.class.getDeclaredField("resources");
            privateField.setAccessible(true); // This allows us to modify private fields
            privateField.set(player1, new int[]{0, 0, 0, 0, 0});
        } catch (Exception e) {
            e.printStackTrace();
        }
        MoveRobberDto moveRobberDto = new MoveRobberDto(0, true);
        gameLogicController.makeMove(moveRobberDto, player1);

        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, player1.getResources());
    }

    @Test
    void testRollOf7RemovesHalfResources() {
        try {
            Field privateField = Player.class.getDeclaredField("resources");
            privateField.setAccessible(true); // This allows us to modify private fields
            privateField.set(player1, new int[]{2,2,2,2,1});
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
        moveDto = new RollDiceDto(7, new MoveRobberDto(18, true));
        gameLogicController.makeMove(moveDto,player1);
        int totalResources=0;
        //The Number of reduced resources should be rounded down, so for a total of 9, 4 should be removed and 5 should be left
        for(Integer i : player1.getResources())totalResources+=i;
        assertEquals(5, totalResources);
    }

    @Test
    void testRollOf7DoesNotRemoveHalfResourcesIfLessThan8Resources(){
        moveDto = new RollDiceDto(7, new MoveRobberDto(18, true));
        gameLogicController.makeMove(moveDto,player1);
        int totalResources=0;
        //Player 2 should have exactly 7 resources from the setup phase, so nothing should be reduced
        for(Integer i : player2.getResources())totalResources+=i;
        assertEquals(7, totalResources);
    }

    @Test
    void testIllegalRobberMoveGetsProperlyPunishedWhenAccused(){
        moveDto = new MoveRobberDto(18, false);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new AccuseCheatingDto();
        gameLogicController.makeMove(moveDto, player1);

        int totalResources=0;
        //Player 2 should have exactly 7 resources from the setup phase, so nothing should be reduced
        for(Integer i : player1.getResources())totalResources+=i;
        assertEquals(3, totalResources);
    }

    @Test
    void testLegalRobberMoveDoesNotGetPunishedWhenAccused(){
        moveDto = new MoveRobberDto(18, true);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new AccuseCheatingDto();
        gameLogicController.makeMove(moveDto, player1);

        int totalResources=0;
        //Player 2 should have exactly 7 resources from the setup phase, so nothing should be reduced
        for(Integer i : player2.getResources())totalResources+=i;
        assertEquals(7, totalResources);
    }

    @Test
    void testFalseAccusationGetsProperlyPunished(){
        moveDto = new MoveRobberDto(18, true);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new AccuseCheatingDto();
        gameLogicController.makeMove(moveDto, player1);

        int totalResources=0;
        //Player 2 should have exactly 7 resources from the setup phase, so nothing should be reduced
        for(Integer i : player1.getResources())totalResources+=i;
        assertEquals(3, totalResources);
    }

    //#####################################################################################################
    private void finishSetUpPhase() {
        moveDto = new BuildVillageMoveDto(9);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(12);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildVillageMoveDto(13);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(21);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(22);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(29);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(29);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(36);
        gameLogicController.makeMove(moveDto, player1);
    }

    private void createPreSetupBoard() {
        //URL of picture of Board:
        //https://cdn.discordapp.com/attachments/1219917626424164376/1231297808997421297/image.png?ex=66367272&is=6623fd72&hm=5989f819604eda76f0d834755e973aaf04f18479a42c26912a5b8a0dc1576799&
        //Only Hexagons are correct in the picture, villages and roads arent :]
        List<Hexagon> hexagonList = new ArrayList<>();
        List<HexagonType> hexagonTypes = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy hexagonTypes and values lists to ensure original lists remain unchanged
        Collections.addAll(hexagonTypes, HexagonType.PASTURE, HexagonType.FOREST, HexagonType.HILLS,
                HexagonType.MOUNTAINS, HexagonType.HILLS, HexagonType.FOREST, HexagonType.HILLS,
                HexagonType.FOREST, HexagonType.HILLS, HexagonType.FIELDS, HexagonType.PASTURE, HexagonType.FIELDS,
                HexagonType.FIELDS, HexagonType.PASTURE, HexagonType.DESERT, HexagonType.FIELDS,
                HexagonType.MOUNTAINS, HexagonType.PASTURE, HexagonType.FOREST);
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        for (int i = 0; i < hexagonTypes.size(); i++) {
            HexagonType hexagonType = hexagonTypes.get(i);
            int value;
            if (hexagonType == HexagonType.DESERT) {
                value = 0; // Desert hexagonType should have value 0
            } else {
                value = values.remove(0);
            }

            ResourceDistribution resourceDistribution = switch (hexagonType) {
                case FIELDS -> ResourceDistribution.FIELDS;
                case PASTURE -> ResourceDistribution.PASTURE;
                case FOREST -> ResourceDistribution.FOREST;
                case HILLS -> ResourceDistribution.HILLS;
                case MOUNTAINS -> ResourceDistribution.MOUNTAINS;
                default -> ResourceDistribution.DESERT;
            };
            hexagonList.add(new Hexagon(hexagonType, resourceDistribution, value, i, false));
        }

        try {
            Field privateField = Board.class.getDeclaredField("hexagonList");
            privateField.setAccessible(true); // This allows us to modify private fields
            privateField.set(gameLogicController.getBoard(), hexagonList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
