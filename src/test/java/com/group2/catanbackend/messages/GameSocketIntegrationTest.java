package com.group2.catanbackend.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catanbackend.config.Constants;
import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.model.PlayerState;
import com.group2.catanbackend.service.GameService;
import com.group2.catanbackend.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameSocketIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameService gameService;

    private final String WEBSOCKET_URI = "ws://localhost:%d/catan";
    private final String WEBSOCKET_TOPIC_PREFIX = "/topic/game/";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void testReceivesNotificationOnNewPlayerJoined() throws Exception{
        JoinResponseDto responseDto = gameService.createAndJoin(new CreateRequestDto("Player1"));

        TestClientImplementation client = new TestClientImplementation(port, responseDto.getToken());

        BlockingQueue<MessageDto> queue = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<MessageDto> handler = new StompFrameHandlerImpl<>(queue, MessageDto.class);
        client.subscribe(Constants.TOPIC_GAME_LOBBY.formatted(responseDto.getGameID()), handler);

        Thread.sleep(1000);

        gameService.joinGame(new JoinRequestDto("Player2", responseDto.getGameID()));
        MessageDto dto = queue.poll(2, TimeUnit.SECONDS);
        assertThat(dto.getClass()).isEqualTo(PlayersInLobbyDto.class);
        PlayersInLobbyDto playersDto = (PlayersInLobbyDto) dto;
        assertThat(playersDto.getPlayers().size()).isEqualTo(2);
        assertThat(playersDto.getPlayers().get(1).getState()).isEqualTo(PlayerState.SOFT_JOINED);
    }

    @Test
    void testReceivesNotificationOnPlayerLeft() throws Exception{
        JoinResponseDto player1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        JoinResponseDto player2 = gameService.joinGame(new JoinRequestDto("Player1", player1.getGameID()));

        TestClientImplementation client = new TestClientImplementation(port, player2.getToken());
        BlockingQueue<MessageDto> queue = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<MessageDto> handler = new StompFrameHandlerImpl<>(queue, MessageDto.class);
        client.subscribe(Constants.TOPIC_GAME_LOBBY.formatted(player2.getGameID()), handler);

        Thread.sleep(1000); //To process subscription;

        gameService.leaveGame(player1.getToken());

        MessageDto dto = queue.poll(2, TimeUnit.SECONDS);
        assertThat(dto.getClass()).isEqualTo(PlayersInLobbyDto.class);
        PlayersInLobbyDto playersInLobbyDto = (PlayersInLobbyDto) dto;
        assertThat(playersInLobbyDto.getPlayers().size()).isEqualTo(1);
        assertThat(playersInLobbyDto.getAdmin().getInGameID()).isEqualTo(player2.getInGameID());
    }
    @Test
    void testReceivesPlayerStateConnectOnSocketEstablished() throws Exception{
        JoinResponseDto player1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        JoinResponseDto player2 = gameService.joinGame(new JoinRequestDto("Player1", player1.getGameID()));

        TestClientImplementation client = new TestClientImplementation(port, player1.getToken());
        BlockingQueue<MessageDto> queue = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<MessageDto> handler = new StompFrameHandlerImpl<>(queue, MessageDto.class);
        client.subscribe(Constants.TOPIC_GAME_LOBBY.formatted(player1.getGameID()), handler);

        TestClientImplementation client2 = new TestClientImplementation(port, player2.getToken());
        BlockingQueue<MessageDto> queue2 = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<MessageDto> handler2 = new StompFrameHandlerImpl<>(queue, MessageDto.class);
        client2.subscribe(Constants.FULL_USER_QUEUE_PATH, handler);

        MessageDto dto = queue.poll(2, TimeUnit.SECONDS);
        assertThat(dto.getClass()).isEqualTo(PlayersInLobbyDto.class);
        PlayersInLobbyDto playersInLobbyDto = (PlayersInLobbyDto) dto;
        assertThat(playersInLobbyDto.getPlayers().get(1).getState()).isEqualTo(PlayerState.CONNECTED);
    }

    @Test
    void testOnceConnectionLostPlayerLeaves() throws Exception{
        JoinResponseDto player1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        TestClientImplementation clientImplementation = new TestClientImplementation(port, player1.getToken());
        clientImplementation.disconnect();

        Thread.sleep(2000);
        assertThat(gameService.getRegisteredGames().get(player1.getGameID())).isNull(); //as the game is deleted
    }


    @Test
    void testReceivesNotificationOnGameStart() throws Exception{
        JoinResponseDto player1 = gameService.createAndJoin(new CreateRequestDto("Player1"));
        JoinResponseDto player2 = gameService.joinGame(new JoinRequestDto("Player2", player1.getGameID()));

        TestClientImplementation client = new TestClientImplementation(port, player2.getToken());
        BlockingQueue<MessageDto> queue = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<MessageDto> handler = new StompFrameHandlerImpl<>(queue, MessageDto.class);
        client.subscribe(Constants.TOPIC_GAME_LOBBY.formatted(player2.getGameID()), handler);

        gameService.startGame(player1.getToken()); //as Player1 is admin
        Thread.sleep(1000);
        MessageDto dto = queue.poll(2, TimeUnit.SECONDS);
        assertThat(dto.getClass()).isEqualTo(GameStartedDto.class);
    }


}