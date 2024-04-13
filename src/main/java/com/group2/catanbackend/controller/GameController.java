package com.group2.catanbackend.controller;

import com.group2.catanbackend.dto.*;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.GameService;
import com.group2.catanbackend.service.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/catan/game")
@AllArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;
    private final TokenService tokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @PostMapping("/create")
    private ResponseEntity<GameSocketEndpointDto> createGame(@Valid @RequestBody CreateRequestDto request) throws GameException {
        String gameId = gameService.createGame();
        JoinRequestDto joinRequestDto = new JoinRequestDto(request.getPlayerName(), gameId);
        return joinGame(joinRequestDto);
    }

    @PostMapping("/connect")
    private ResponseEntity<GameSocketEndpointDto> joinGame(@Valid @RequestBody JoinRequestDto joinRequest) throws GameException {
        String token = tokenService.generateToken();

        Player p = gameService.joinGame(token, joinRequest);
        tokenService.pushToken(token, p);

        simpMessagingTemplate.convertAndSend("/topic/game/" + joinRequest.getGameID() + "/messages", joinRequest.getPlayerName() + " joined");
        GameSocketEndpointDto endpoint = new GameSocketEndpointDto(joinRequest.getGameID(), joinRequest.getPlayerName(), token);
        return ResponseEntity.ok(endpoint);
    }

    @PostMapping("/start")
    private ResponseEntity<Object> startGame(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        String gameID = tokenService.getPlayerByToken(token).getGameID();
        gameService.startGame(token, gameID);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/list")
    private ResponseEntity<ListGameResponse> getGames(){
        List<LobbyDto> lobbies = gameService.getLobbies();
        ListGameResponse response = new ListGameResponse();
        response.setGameList(lobbies);
        response.setCount(lobbies.size());
        return ResponseEntity.ok(response);
    }






}
