package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.exception.NoSuchGameException;
import com.group2.catanbackend.exception.NoSuchTokenException;
import com.group2.catanbackend.exception.NotAuthorizedException;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.model.PlayerState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;

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

        Mockito.when(tokenService.generateToken()).thenReturn("myToken2");
        JoinResponseDto response2 = gameService.joinGame(new JoinRequestDto("Player2", response1.getGameID()));
        Mockito.when(tokenService.getPlayerByToken(response2.getToken())).thenReturn(new Player(response2.getToken(), response2.getPlayerName(), response2.getGameID()));

        String token = response2.getToken();

        assertThrows(NotAuthorizedException.class, () -> {gameService.startGame(token);});
    }

    @Test
    void gameRemovedOnceGameOverByDisconnect(){
        Player p1 = new Player("token1", "player1", "1");
        p1.setInGameID(0);
        Player p2 = new Player("token2", "player2", "1");
        p2.setInGameID(1);

        p1.setPlayerState(PlayerState.CONNECTED);
        p2.setPlayerState(PlayerState.CONNECTED);
        RunningInstanceService s = new RunningInstanceService(messagingService);
        s.setGameId("1");
        s.addPlayers(List.of(p1, p2));
        gameService.getRunningGames().put("1", s);
        s.start();

        Mockito.when(tokenService.getPlayerByToken("token1")).thenReturn(p1);
        Mockito.when(tokenService.getPlayerByToken("token2")).thenReturn(p2);

        gameService.handleConnectionLost("token1");
        gameService.handleConnectionLost("token2");

        assertFalse(gameService.getRunningGames().containsKey("1"));

        Mockito.verify(tokenService).revokeAll("1");

    }
}