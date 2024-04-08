package com.group2.catanbackend.config;

import com.group2.catanbackend.service.TokenService;
import com.sun.security.auth.UserPrincipal;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.security.Principal;
import java.util.Map;

import static com.group2.catanbackend.config.Util.extractToken;


@Slf4j
@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    private final TokenService tokenService;
    public UserHandshakeHandler(@Autowired TokenService tokenService){
        this.tokenService = tokenService;
    }
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);

        if(tokenService.tokenExists(token))
            return new UserPrincipal(token);
        return null;
    }


}
