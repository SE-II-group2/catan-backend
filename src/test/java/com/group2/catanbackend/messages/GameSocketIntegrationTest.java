package com.group2.catanbackend.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catanbackend.config.Constants;
import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.dto.game.MessageDto;
import com.group2.catanbackend.dto.game.MessageType;
import com.group2.catanbackend.dto.game.PlayerEventDto;
import com.group2.catanbackend.dto.game.PlayersInLobbyDto;
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
    public void testReceivesNotificationOnNewPlayerJoined() throws Exception{
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
        PlayerEventDto eventDto = playersDto.getEvent();
        assertThat(eventDto.getType()).isEqualTo(PlayerEventDto.Type.PLAYER_JOINED);
    }

    @Test
    public void testReceivesNotificationOnPlayerLeft() throws Exception{
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
        assertThat(playersInLobbyDto.getEvent().getType()).isEqualTo(PlayerEventDto.Type.PLAYER_LEFT);
        assertThat(playersInLobbyDto.getEvent().getPlayer().getInGameID()).isEqualTo(player1.getInGameID());
    }

}