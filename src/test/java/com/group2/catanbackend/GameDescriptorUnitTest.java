package com.group2.catanbackend;

import com.group2.catanbackend.exception.GameFullException;
import com.group2.catanbackend.exception.PlayerAlreadyInGameException;
import com.group2.catanbackend.model.GameDescriptor;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDescriptorUnitTest {
    private GameDescriptor gameDescriptor;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;

    @BeforeEach
    void setUp() {
        gameDescriptor = new GameDescriptor();
        player1 = new Player("token1", "player1", "gameID");
        player2 = new Player("token2", "player2", "gameID");
        player3 = new Player("token3", "player3", "gameID");
        player4 = new Player("token4", "player4", "gameID");
        player5 = new Player("token5", "player5", "gameID");
    }

    @Test
    void joinGameSuccessfully() {
        gameDescriptor.join(player1);
        assertEquals(1, gameDescriptor.getPlayerCount());
    }

    @Test
    void joinGameAsAdmin() {
        gameDescriptor.join(player1);
        assertEquals("token1", gameDescriptor.getAdmin().getToken());
    }

    @Test
    void joinGamePlayerAlreadyInGame() {
        gameDescriptor.join(player1);
        assertThrows(PlayerAlreadyInGameException.class, () -> gameDescriptor.join(player1));
    }

    @Test
    void joinGameWhenFull() {
        gameDescriptor.join(player1);
        gameDescriptor.join(player2);
        gameDescriptor.join(player3);
        gameDescriptor.join(player4);
        assertThrows(GameFullException.class, () -> gameDescriptor.join(player5));
    }
}