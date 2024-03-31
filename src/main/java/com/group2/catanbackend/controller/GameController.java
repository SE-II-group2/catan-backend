package com.group2.catanbackend.controller;

import com.group2.catanbackend.dto.GameSocketEndpointDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.service.GameService;
import com.group2.catanbackend.service.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/catan/game")
@AllArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;
    private final TokenService tokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @PostMapping("/create")
    @ResponseBody
    private ResponseEntity<GameSocketEndpointDto> createGame(@Valid @RequestBody CreateRequestDto request) throws GameException {
        String gameId = gameService.createGame();
        JoinRequestDto joinRequestDto = new JoinRequestDto(request.getPlayerName(), gameId);
        return joinGame(joinRequestDto);
    }

    @PostMapping("/connect")
    @ResponseBody
    private ResponseEntity<GameSocketEndpointDto> joinGame(@Valid @RequestBody JoinRequestDto joinRequest) throws GameException {
        String token = tokenService.generateToken();

        gameService.joinGame(token, joinRequest);

        tokenService.pushToken(token, joinRequest);

        simpMessagingTemplate.convertAndSend("/topic/game/" + joinRequest.getGameID() + "/messages", joinRequest.getPlayerName());
        GameSocketEndpointDto endpoint = new GameSocketEndpointDto(joinRequest.getGameID(), joinRequest.getPlayerName(), token);
        return ResponseEntity.ok(endpoint);
    }





}
