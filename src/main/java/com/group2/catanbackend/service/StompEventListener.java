package com.group2.catanbackend.service;

import com.group2.catanbackend.model.GameDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

import static com.group2.catanbackend.config.Constants.FULL_USER_QUEUE_PATH;
import static com.group2.catanbackend.config.Constants.QUEUE_USER_MESSAGE;

@Slf4j
@Component
public class StompEventListener {
    GameService gameService;
    private static final String KEY_SUBSCRIPTION_PATH = "simpDestination";

    public StompEventListener(@Autowired GameService gameService){
        this.gameService = gameService;
    }
    @EventListener
    public void onClientSubscribe(SessionSubscribeEvent event){
        String destination = event.getMessage().getHeaders().get(KEY_SUBSCRIPTION_PATH, String.class);
        if(FULL_USER_QUEUE_PATH.equals(destination) && event.getUser() != null){
           String token = event.getUser().getName();
           gameService.handleConnectionEstablished(token);
        }
    }

    @EventListener
    public void onClientDisconnect(SessionDisconnectEvent event){
        if(event.getUser() != null){
            gameService.handleConnectionLost(event.getUser().getName());
        }
    }
}
