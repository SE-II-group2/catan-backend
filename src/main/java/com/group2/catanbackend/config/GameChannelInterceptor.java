package com.group2.catanbackend.config;

import com.group2.catanbackend.exception.SubscriptionDeniedException;
import com.group2.catanbackend.service.TokenService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class GameChannelInterceptor implements ChannelInterceptor {

    private final TokenService tokenService;
    public GameChannelInterceptor(@Autowired TokenService tokenService){
        this.tokenService = tokenService;
    }
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            String token = getToken(accessor.getUser());
            String gameID = extractGameId(accessor.getDestination());

            if(gameID != null){
                if(token == null)
                    throw new SubscriptionDeniedException("Subscription without user");
                if(tokenService.validateToken(token, gameID))
                    return message;
                throw new SubscriptionDeniedException("User " + token + " cannot subscribe to game " + gameID);
            }
        }
        return message;
    }

    private String getToken(Principal principal){
        if(principal == null)
            return null;
        return principal.getName();
    }

    private String extractGameId(@Nullable String destination){
        if(destination == null)
            return null;

        String[] segments = destination.split("/");
        if(segments.length >= 4 && segments[2].equals("game")){
            return segments[3];
        }
        return null;
    }
}
