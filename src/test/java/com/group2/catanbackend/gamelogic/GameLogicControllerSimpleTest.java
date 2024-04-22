package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NotActivePlayerException;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.gamelogic.objects.Road;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class GameLogicControllerSimpleTest {

    GameLogicController gameLogicController;
    private Player player1;
    private Player player2;
    GameMoveDto moveDto;
    private ArrayList<Player> playersList = new ArrayList<Player>();


    @BeforeEach
    public void setUp() {
        player1 = new Player("Token1", "Player One(1)", "this");
        player2 = new Player("Token2", "Player Two(2)", "this");
        playersList.add(player1);
        playersList.add(player2);
        gameLogicController = new GameLogicController(playersList, null, "this");
    }

    @Test
    public void testAddSingleSimpleRoad(){
        moveDto = new BuildRoadMoveDto(0,1);
        gameLogicController.makeMove(moveDto, player1);
        Board board = gameLogicController.getBoard();

        assertTrue(board.getAdjacencyMatrix()[0][1] instanceof Road);
        assertEquals(gameLogicController.getSetupPhaseTurnOrder().get(0), player2);
    }

    @Test
    public void testAddMultipleRoads(){
        moveDto = new BuildRoadMoveDto(0,1);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(3,4);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(4,12);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(0,8);
        gameLogicController.makeMove(moveDto, player1);
        Board board = gameLogicController.getBoard();

        assertTrue(board.getAdjacencyMatrix()[0][1] instanceof Road);
        assertTrue(board.getAdjacencyMatrix()[3][4] instanceof Road);
        assertTrue(board.getAdjacencyMatrix()[4][12] instanceof Road);
        assertTrue(board.getAdjacencyMatrix()[0][8] instanceof Road);

        assertEquals(0, gameLogicController.getSetupPhaseTurnOrder().size());
        assertEquals(player1, gameLogicController.getTurnOrder().get(0));
    }

    @Test
    public void testFullSetUpPhase(){
        moveDto = new BuildVillageMoveDto(0,2);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(0,1);
        gameLogicController.makeMove(moveDto, player1);

        moveDto = new BuildVillageMoveDto(0,5);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(3,4);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(1,6);
        gameLogicController.makeMove(moveDto, player2);
        moveDto = new BuildRoadMoveDto(4,12);
        gameLogicController.makeMove(moveDto, player2);

        moveDto = new BuildVillageMoveDto(1,3);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(0,8);
        gameLogicController.makeMove(moveDto, player1);
        Board board = gameLogicController.getBoard();

        assertTrue(board.getAdjacencyMatrix()[0][1] instanceof Road);
        assertTrue(board.getAdjacencyMatrix()[3][4] instanceof Road);
        assertTrue(board.getAdjacencyMatrix()[4][12] instanceof Road);
        assertTrue(board.getAdjacencyMatrix()[0][8] instanceof Road);

        assertTrue(board.getIntersections()[0][2] instanceof Building);
        assertTrue(board.getIntersections()[0][5] instanceof Building);
        assertTrue(board.getIntersections()[1][6] instanceof Building);
        assertTrue(board.getIntersections()[1][3] instanceof Building);

        assertEquals(0, gameLogicController.getSetupPhaseTurnOrder().size());
        assertEquals(player1, gameLogicController.getTurnOrder().get(0));
    }

    @Test
    public void testTurnOrderSetupPhaseExceptionThrow(){
        moveDto = new BuildRoadMoveDto(0,1);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(3,4);
        assertThrows(NotActivePlayerException.class , ()->gameLogicController.makeMove(moveDto, player1));

        moveDto = new BuildVillageMoveDto(3,4);
        assertThrows(NotActivePlayerException.class , ()->gameLogicController.makeMove(moveDto, player1));

        moveDto = new EndTurnMoveDto();
        assertThrows(InvalidGameMoveException.class , ()->gameLogicController.makeMove(moveDto, player1));
    }

    @Test
    public void testRollDiceDuringSetupPhase() {
        moveDto = new RollDiceDto(7);
        assertThrows(InvalidGameMoveException.class, () -> gameLogicController.makeMove(moveDto, player1));
    }

}
