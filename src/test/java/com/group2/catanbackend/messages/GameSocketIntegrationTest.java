package com.group2.catanbackend.messages;

import com.group2.catanbackend.config.Constants;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.game.PlayersInLobbyDto;
import com.group2.catanbackend.model.Player;
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
        String gameID = gameService.createGame();
        JoinRequestDto joinDto1 = new JoinRequestDto("player1", gameID);
        String token1 = tokenService.generateToken();
        Player p1 = gameService.joinGame(token1, joinDto1);
        tokenService.pushToken(token1, p1);

        TestClientImplementation client = new TestClientImplementation(port, token1);

        BlockingQueue<PlayersInLobbyDto> queue = new LinkedBlockingQueue<>();
        StompFrameHandlerImpl<PlayersInLobbyDto> handler = new StompFrameHandlerImpl<>(queue, PlayersInLobbyDto.class);
        client.subscribe(Constants.TOPIC_GAME_LOBBY.formatted(gameID), handler);

        Thread.sleep(1000);

        JoinRequestDto joinDto2 = new JoinRequestDto("player2", gameID);
        String token2 = tokenService.generateToken();
        gameService.joinGame(token2, joinDto2);
        Thread.sleep(1000);
        System.out.println("test");
        PlayersInLobbyDto dto = queue.poll(2, TimeUnit.SECONDS);
        assertThat(dto.getPlayers().size()).isEqualTo(2);
    }
}