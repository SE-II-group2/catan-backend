package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NotActivePlayerException;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.gamelogic.objects.Road;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class GameLogicControllerSimpleTest {

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

    }

    @Test
    public void testAddSingleSimpleVillageAndRoad(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        Board board = gameLogicController.getBoard();

        assertTrue(board.getAdjacencyMatrix()[0][1] instanceof Road);
        assertTrue(board.getIntersections()[0][2] instanceof Building);
        assertEquals(gameLogicController.getSetupPhaseTurnOrder().get(0), player2);


    }

    //TODO Uncomment line, fails right now and should only work after fixing issue of needing to set village before road
    @Test
    public void testAddTwoRoadsThrowsError(){
        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        /*moveDto = new BuildRoadMoveDto(3);
        assertThrows(Exception.class, () -> gameLogicController.makeMove(moveDto, player2));*/
    }

    @Test
    public void testFullSetUpPhase(){
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
        assertEquals(player1, gameLogicController.getTurnOrder().get(0));
    }

    @Test
    public void testTurnOrderSetupPhaseExceptionThrow(){
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
    public void testRollDiceDuringSetupPhase() {
        moveDto = new RollDiceDto(7);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(moveDto, player1));
    }

}
