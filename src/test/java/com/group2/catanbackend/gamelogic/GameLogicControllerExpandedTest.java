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

        assertArrayEquals(new int[]{0, 1, 1, 2, 1}, player1.getResources());
        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, player2.getResources());
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

    // fixme split into two tests for messaging and logic
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
        verify(messagingMock, times(21)).notifyGameProgress(eq(gameLogicController.getGameId()), argumentCaptor.capture()); //4 moves here, already 8 from setup phase, 1 from hexqgonlist

        // fixme avoid catching exceptions as the assertions might not execute
        try {
            List<MessageDto> allValues = argumentCaptor.getAllValues();
            CurrentGameStateDto argument2 = (CurrentGameStateDto) allValues.get(allValues.size() - 6); //get the last buildVillageMoveDto io the setup phase
            //moveDto = new BuildVillageMoveDto(3, 2);
            assertEquals(BuildingType.VILLAGE.name(), argument2.getIntersections().get(29).getBuildingType());
            assertEquals(player1.getDisplayName(), argument2.getIntersections().get(29).getOwner().getDisplayName());

            argument2 = (CurrentGameStateDto) allValues.get(allValues.size() - 9); //get the last buildRoadMoveDto io the setup phase
            //moveDto = new BuildRoadMoveDto(29, 30);
            assertNotNull(argument2.getConnections().get(36).getOwner());
            assertEquals(player1.getDisplayName(), argument2.getConnections().get(36).getOwner().getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // fixme split into two tests for messaging and logic
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
            verify(messagingMock, times(16)).notifyGameProgress(eq(gameLogicController.getGameId()), argumentCaptor.capture()); // 8 for setup phase, 2 for road, 1 from hexagonlist and village and 1 for victory

            List<MessageDto> allValues = argumentCaptor.getAllValues();
            GameoverDto lastArgument =  (GameoverDto) allValues.get(allValues.size() - 1); //get the last Dto sent
            assertEquals(lastArgument.getWinner().getDisplayName(), player1.getDisplayName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // fixme split in individual tests (also for setup conditions)
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
        assertArrayEquals(new int[]{0, 1, 0, 1, 1}, player1.getResources());

        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new RollDiceDto(6);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new EndTurnMoveDto();
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new RollDiceDto(9);
        gameLogicController.makeMove(moveDto, player1);

        assertArrayEquals(new int[]{1, 1, 1, 2, 1}, player1.getResources());
        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, player2.getResources());

        moveDto = new BuildVillageMoveDto(11);
        gameLogicController.makeMove(moveDto, player1);

        assertInstanceOf(Building.class, gameLogicController.getBoard().getIntersections()[1][5]);
        assertArrayEquals(new int[]{0, 0, 0, 1, 1}, player1.getResources());


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

    // fixme too complex setup but youll learn patterns for this in a masters course (you can still ask me if youre interested)
    private void createPreSetupBoard() {
        //URL of picture of Board:
        //https://cdn.discordapp.com/attachments/1219917626424164376/1231297808997421297/image.png?ex=66367272&is=6623fd72&hm=5989f819604eda76f0d834755e973aaf04f18479a42c26912a5b8a0dc1576799&
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
