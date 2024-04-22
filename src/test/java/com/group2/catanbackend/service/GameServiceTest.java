package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.exception.NoSuchGameException;
import com.group2.catanbackend.exception.NoSuchTokenException;
import com.group2.catanbackend.exception.NotAuthorizedException;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.configuration.IMockitoConfiguration;
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
    private RunningInstanceService service;

    @Mock
    private MessagingService messagingService;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGameSuccessfully() {
        JoinResponseDto responseDto = gameService.createAndJoin(new CreateRequestDto("Player1"));
        assertEquals("Player1", responseDto.getPlayerName());
        assertEquals(responseDto.getGameID(), gameService.getRegisteredGames().get(responseDto.getGameID()).getId());
    }

    @Test
    void joinGameNoSuchGame() {
        JoinRequestDto request = new JoinRequestDto("Player1", "invalid");

        assertThrows(NoSuchGameException.class, () -> gameService.joinGame(request));
    }

    @Test
    void startGameSuccessfully() {
        Mockito.when(tokenService.generateToken()).thenReturn("myToken");
        JoinResponseDto response = gameService.createAndJoin(new CreateRequestDto("Player1"));

        Mockito.when(tokenService.getPlayerByToken(response.getToken())).thenReturn(new Player(response.getToken(), response.getPlayerName(), response.getGameID()));
        Mockito.when(applicationContext.getBean("runningInstanceService")).thenReturn(service);


        gameService.startGame(response.getToken());

        assertTrue(gameService.getRunningGames().containsKey(response.getGameID()));
        assertFalse(gameService.getRegisteredGames().containsKey(response.getGameID()));
        Mockito.verify(messagingService).notifyLobby(eq(response.getGameID()), any());
        Mockito.verify(service).addPlayers(any());
        Mockito.verify(service).setGameId(response.getGameID());
        Mockito.verify(service).start();
    }

    @Test
    void startGameWithoutValidToken(){
        assertThrows(NoSuchTokenException.class, ()-> gameService.startGame("notAToken"));
    }

    @Test
    void startNonExistingGame(){
        Mockito.when(tokenService.getPlayerByToken("MyToken")).thenReturn(new Player("MyToken", "PlayerName", "aNonExistingGame"));
        assertThrows(NoSuchGameException.class, () -> gameService.startGame("MyToken"));
    }

    @Test
    void leaveNonExistingGameThrowsNoSuchGame(){
        Mockito.when(tokenService.getPlayerByToken("MyToken")).thenReturn(new Player("MyToken", "PlayerName", "aNonExistingGame"));
        assertThrows(NoSuchGameException.class, () -> gameService.leaveGame("MyToken"));
    }

    @Test
    void startGameNotAuthorized() {
        Mockito.when(tokenService.generateToken()).thenReturn("myToken1");
        JoinResponseDto response1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        //Mockito.when(tokenService.getPlayerByToken(response1.getToken())).thenReturn(new Player(response1.getToken(), response1.getPlayerName(), response1.getGameID()));

        Mockito.when(tokenService.generateToken()).thenReturn("myToken2");
        JoinResponseDto response2 = gameService.joinGame(new JoinRequestDto("Player2", response1.getGameID()));
        Mockito.when(tokenService.getPlayerByToken(response2.getToken())).thenReturn(new Player(response2.getToken(), response2.getPlayerName(), response2.getGameID()));

        assertThrows(NotAuthorizedException.class, () -> {gameService.startGame(response2.getToken());});
    }
}