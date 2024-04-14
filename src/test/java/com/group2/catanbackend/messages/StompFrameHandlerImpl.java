package com.group2.catanbackend.messages;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class StompFrameHandlerImpl<T> implements StompFrameHandler {
    private final BlockingQueue<T> queue;
    private final Class<T> payloadType;

    public StompFrameHandlerImpl(BlockingQueue<T> queue, Class<T> payloadType){
        this.queue = queue;
        this.payloadType = payloadType;
    }
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return payloadType;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        queue.add(payloadType.cast(payload));
    }
}
