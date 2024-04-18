package com.group2.catanbackend.messages;

import com.group2.catanbackend.config.Constants;
import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
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

    @Test
    public void testReceivesNotificationOnNewPlayerJoined() throws Exception{
        JoinResponseDto responseDto = gameService.createAndJoin(new CreateRequestDto("Player1"));

        TestClientImplementation client = new TestClientImplementation(port, responseDto.getToken());

        BlockingQueue<PlayersInLobbyDto> queue = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<PlayersInLobbyDto> handler = new StompFrameHandlerImpl<>(queue, PlayersInLobbyDto.class);
        client.subscribe(Constants.TOPIC_GAME_LOBBY.formatted(responseDto.getGameID()), handler);

        Thread.sleep(1000);

        gameService.joinGame(new JoinRequestDto("Player2", responseDto.getGameID()));
        Thread.sleep(1000);

        PlayersInLobbyDto dto = queue.poll(2, TimeUnit.SECONDS);
        assertThat(dto.getPlayers().size()).isEqualTo(2);
    }
}