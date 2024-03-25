package com.group2.catanbackend;

import com.group2.catanbackend.websocket.StompFrameHandlerClientImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketBrokerIntegrationTest {
    @LocalServerPort
    private int port;

    private final String WEBSOCKET_URI = "ws://localhost:%d/websocket-example-broker";
    private final String WEBSOCKET_TOPIC = "/topic/hello-response";

    /**
     * Message Queue
     */
    BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Test
    public void testWebSocketMessageBroker() throws Exception {
        StompSession session = initStompSession();

        String message = "Test message";
        session.send("/app/hello", message);

        var expectedResponse = "echo from broker: " + message;
        assertThat(messages.poll(1, TimeUnit.SECONDS)).isEqualTo(expectedResponse);
    }


    public StompSession initStompSession() throws Exception{
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSession session = stompClient.connectAsync(String.format(WEBSOCKET_URI, port),
                new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe(WEBSOCKET_TOPIC, new StompFrameHandlerClientImpl(messages));

        return session;
    }
}
