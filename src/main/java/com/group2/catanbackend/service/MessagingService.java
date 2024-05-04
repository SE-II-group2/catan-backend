package com.group2.catanbackend.service;

import com.group2.catanbackend.config.Constants;
import com.group2.catanbackend.dto.game.MessageDto;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class MessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public MessagingService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyLobby(String gameID, MessageDto payload){
        final String destination = Constants.TOPIC_GAME_LOBBY.formatted(gameID);
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void notifyGameProgress(String gameID, MessageDto payload){
        final String destination = Constants.TOPIC_GAME_PROGRESS.formatted(gameID);
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void notifyUser(String token, MessageDto payload){
        final String destination = Constants.QUEUE_USER_MESSAGE;
        messagingTemplate.convertAndSendToUser(token, destination, payload);
    }
}
