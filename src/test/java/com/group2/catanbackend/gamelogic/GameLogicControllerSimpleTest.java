package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NotActivePlayerException;
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

    @Test
    public void testAddTwoRoadsThrowsError(){
        moveDto = new BuildVillageMoveDto(0);
        gameLogicController.makeMove(moveDto,player1);
        moveDto = new BuildRoadMoveDto(0);
        gameLogicController.makeMove(moveDto, player1);
        moveDto = new BuildRoadMoveDto(3);
        assertThrows(Exception.class, () -> gameLogicController.makeMove(moveDto, player2));
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
        assertEquals(player1, gameLogicController.getActivePlayer());
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
    }
    @Test
    void testDisconnectInSetupPhaseMakesPlayerNeverToBecomeActiveAgain_playerNotOnTurn(){
        //no exception should be thrown.
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


}
