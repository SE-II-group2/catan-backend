package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.gamelogic.enums.Location;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.gamelogic.objects.Hexagon;
import com.group2.catanbackend.gamelogic.objects.Road;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class GameLogicControllerExpandedTest {

    GameLogicController gameLogicController;
    private Player player1;
    private Player player2;
    GameMoveDto moveDto;
    private final ArrayList<Player> playersList = new ArrayList<>();
    @Mock
    MessagingService messagingService;


    @BeforeEach
    public void setUp() {
        player1 = new Player("Token1", "Player One(1)", "this");
        player1.setInGameID(1);
        player2 = new Player("Token2", "Player Two(2)", "this");
        player2.setInGameID(1);
        playersList.add(player1);
        playersList.add(player2);
        messagingService = mock(MessagingService.class);
        gameLogicController = new GameLogicController(playersList, messagingService, "this");
        createPreSetupBoard();
        finishSetUpPhase();
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

        moveDto = new BuildRoadMoveDto(10, 11);
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

        moveDto = new BuildVillageMoveDto(1, 5);
        gameLogicController.makeMove(moveDto, player1);

        assertTrue(gameLogicController.getBoard().getIntersections()[1][5] instanceof Building);
        assertArrayEquals(new int[]{0, 0, 0, 1, 1}, player1.getResources());


    }


    //#####################################################################################################
    private void finishSetUpPhase() {
        moveDto = new BuildVillageMoveDto(1, 3);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(9, 10);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildVillageMoveDto(1, 7);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(13, 23);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(2, 6);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(22, 33);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(3, 2);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(29, 30);
        gameLogicController.makeMove(moveDto, player1);
    }

    private void createPreSetupBoard() {
        //URL of picture of Board:
        //https://cdn.discordapp.com/attachments/1219917626424164376/1231297808997421297/image.png?ex=66367272&is=6623fd72&hm=5989f819604eda76f0d834755e973aaf04f18479a42c26912a5b8a0dc1576799&
        List<Hexagon> hexagonList = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy locations and values lists to ensure original lists remain unchanged
        Collections.addAll(locations, Location.PASTURE, Location.FOREST, Location.HILLS,
                Location.MOUNTAINS, Location.HILLS, Location.FOREST, Location.HILLS,
                Location.FOREST, Location.HILLS, Location.FIELDS, Location.PASTURE, Location.FIELDS,
                Location.FIELDS, Location.PASTURE, Location.DESERT, Location.FIELDS,
                Location.MOUNTAINS, Location.PASTURE, Location.FOREST);
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        for (int i = 0; i<locations.size(); i++) {
            Location location = locations.get(i);
            int value;
            if (location == Location.DESERT) {
                value = 0; // Desert location should have value 0
            } else {
                value = values.remove(0);
            }

            ResourceDistribution resourceDistribution = switch (location) {
                case FIELDS -> ResourceDistribution.FIELDS;
                case PASTURE -> ResourceDistribution.PASTURE;
                case FOREST -> ResourceDistribution.FOREST;
                case HILLS -> ResourceDistribution.HILLS;
                case MOUNTAINS -> ResourceDistribution.MOUNTAINS;
                default -> ResourceDistribution.DESERT;
            };
            hexagonList.add(new Hexagon(location, resourceDistribution, value, i));
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
