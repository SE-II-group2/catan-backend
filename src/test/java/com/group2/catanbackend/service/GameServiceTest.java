package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.exception.NoSuchGameException;
import com.group2.catanbackend.exception.NotAuthorizedException;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MessagingService messagingService;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGameSuccessfully() {
        String gameId = gameService.createGame();
        assertNotNull(gameId);
    }

    @Test
    void joinGameSuccessfully() {
        String gameId = gameService.createGame();
        JoinRequestDto request = new JoinRequestDto();
        request.setGameID(gameId);
        request.setPlayerName("player1");

        Player player = gameService.joinGame("token1", request);

        assertEquals("player1", player.getDisplayName());
    }

    @Test
    void joinGameNoSuchGame() {
        JoinRequestDto request = new JoinRequestDto();
        request.setGameID("invalid");
        request.setPlayerName("player1");

        assertThrows(NoSuchGameException.class, () -> gameService.joinGame("token1", request));
    }

    @Test
    void startGameSuccessfully() {
        String gameId = gameService.createGame();
        JoinRequestDto request = new JoinRequestDto();
        request.setGameID(gameId);
        request.setPlayerName("admin");

        gameService.joinGame("token1", request);
        gameService.startGame("token1", gameId);

        assertTrue(gameService.getRunningGames().containsKey(gameId));
        assertFalse(gameService.getRegisteredGames().containsKey(gameId));
        Mockito.verify(messagingService).notifyLobby(eq(gameId), any());
    }

    @Test
    void startGameNotAuthorized() {
        String gameId = gameService.createGame();
        JoinRequestDto request = new JoinRequestDto();
        request.setGameID(gameId);
        request.setPlayerName("player1");

        gameService.joinGame("token1", request);

        assertThrows(NotAuthorizedException.class, () -> gameService.startGame("token2", gameId));
    }
}