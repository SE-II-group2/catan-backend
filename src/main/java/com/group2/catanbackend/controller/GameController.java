package com.group2.catanbackend.controller;

import com.group2.catanbackend.dto.*;
import com.group2.catanbackend.dto.game.GameMoveDto;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.service.GameService;
import com.group2.catanbackend.service.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<JoinResponseDto> createGame(@Valid @RequestBody CreateRequestDto request) throws GameException {
        return ResponseEntity.ok(gameService.createAndJoin(request));
    }

    @PostMapping("/connect")
    public ResponseEntity<JoinResponseDto> joinGame(@Valid @RequestBody JoinRequestDto joinRequest) throws GameException {
        return ResponseEntity.ok(gameService.joinGame(joinRequest));
    }

    @PostMapping("/leave")
    public ResponseEntity<Object> leaveGame(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        gameService.leaveGame(token);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/start")
    public ResponseEntity<Object> startGame(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        gameService.startGame(token);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/gamemove")
    public ResponseEntity<Object> makeMove(@Valid @RequestBody GameMoveDto gameMoveDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws GameException{
        return ResponseEntity.ok(gameService.makeMove(token, gameMoveDto));
    }

    @GetMapping("/list")
    public ResponseEntity<ListGameResponse> getGames(){
        List<LobbyDto> lobbies = gameService.getLobbies();
        ListGameResponse response = new ListGameResponse();
        response.setGameList(lobbies);
        response.setCount(lobbies.size());
        return ResponseEntity.ok(response);
    }
}
