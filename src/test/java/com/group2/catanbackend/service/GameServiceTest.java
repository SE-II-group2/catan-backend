package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.exception.NoSuchGameException;
import com.group2.catanbackend.exception.NotAuthorizedException;
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
    private TokenService tokenService;

    @Mock
    private MessagingService messagingService;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGameSuccessfully() {
        JoinResponseDto responseDto = gameService.createAndJoin(new CreateRequestDto("Player1"));
        assertEquals("Player1", responseDto.getPlayerName());
        assertEquals(responseDto.getGameID(), gameService.getRegisteredGames().get(0).getId());
    }

    @Test
    void joinGameNoSuchGame() {
        JoinRequestDto request = new JoinRequestDto("Player1", "invalid");

        assertThrows(NoSuchGameException.class, () -> gameService.joinGame(request));
    }

    @Test
    void startGameSuccessfully() {
        JoinResponseDto response = gameService.createAndJoin(new CreateRequestDto("Player1"));
        gameService.startGame(response.getToken());

        assertTrue(gameService.getRunningGames().containsKey(response.getGameID()));
        assertFalse(gameService.getRegisteredGames().containsKey(response.getGameID()));
        Mockito.verify(messagingService).notifyLobby(eq(response.getGameID()), any());
    }

    @Test
    void startGameNotAuthorized() {
        JoinResponseDto responseDto1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        JoinResponseDto responseDto2 = gameService.joinGame(new JoinRequestDto("Player2", responseDto1.getGameID()));

        assertEquals(responseDto1.getToken(), gameService.getRegisteredGames().get(responseDto1.getGameID()).getAdmin().getToken());
        assertThrows(NotAuthorizedException.class, () -> gameService.startGame(responseDto2.getToken()));
    }
}