package com.group2.catanbackend.messages;

import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.TimeUnit;

public class TestClientImplementation {
    private static final String SOCKET_URL = "ws://localhost:%d/catan";
    private final StompSession session;

    public TestClientImplementation(int port, String token) throws Exception{
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, token);

        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());
        this.session = client.connectAsync(SOCKET_URL.formatted(port), headers, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);
    }

    public void subscribe(String destination, StompFrameHandler handler){
        session.subscribe(destination, handler);
    }
}
