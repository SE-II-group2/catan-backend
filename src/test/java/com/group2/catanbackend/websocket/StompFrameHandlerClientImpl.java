package com.group2.catanbackend.websocket;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class StompFrameHandlerClientImpl implements StompFrameHandler {
    private final BlockingQueue<String> messageQueue;

    public StompFrameHandlerClientImpl(BlockingQueue<String> receivedMessagesQueue){
        messageQueue = receivedMessagesQueue;
    }

    @Override
    public Type getPayloadType(StompHeaders headers){
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload){
        messageQueue.add((String) payload);
    }
}
