package com.group2.catanbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

import static com.group2.catanbackend.config.Constants.FULL_USER_QUEUE_PATH;

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
        Principal user = event.getUser();
        if(FULL_USER_QUEUE_PATH.equals(destination) && user != null){
           String token = user.getName();
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
