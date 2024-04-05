package com.group2.catanbackend.controller;

import com.group2.catanbackend.dto.*;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.service.GameService;
import com.group2.catanbackend.service.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

        simpMessagingTemplate.convertAndSend("/topic/game/" + joinRequest.getGameID() + "/messages", joinRequest.getPlayerName() + " joined");
        GameSocketEndpointDto endpoint = new GameSocketEndpointDto(joinRequest.getGameID(), joinRequest.getPlayerName(), token);
        return ResponseEntity.ok(endpoint);
    }

    @GetMapping("/list")
    private ResponseEntity<ListGameResponse> getGames(){
        List<Game> games = gameService.getGames();
        ListGameResponse response = new ListGameResponse();
        response.setGameList(games);
        response.setCount(games.size());
        return ResponseEntity.ok(response);
    }






}
