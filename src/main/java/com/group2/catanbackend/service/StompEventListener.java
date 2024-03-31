package com.group2.catanbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;
@Slf4j
@Component
public class StompEventListener implements ApplicationListener<SessionConnectedEvent> {
    @Override
    public void onApplicationEvent(SessionConnectedEvent event){
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userID = null;

        Principal principal = sha.getUser();
        if(principal != null)
            userID = principal.getName();

        log.info("STOMP event: " + sha.getCommand() + " from " + userID);
    }
}
