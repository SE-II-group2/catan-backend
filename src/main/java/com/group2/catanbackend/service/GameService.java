package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.Game;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.NoSuchGameException;
import com.group2.catanbackend.model.GameDescriptor;
import com.group2.catanbackend.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Scope("singleton")
public class GameService {

    private final Map<String, GameDescriptor> registeredGames = new HashMap<>();

    public String createGame(){
        GameDescriptor game = new GameDescriptor();
        String id = game.getId();
        log.info("Created game: " + id);
        registeredGames.put(id, game);
        return id;
    }

    public void joinGame(String token, JoinRequestDto request) throws RuntimeException {
        GameDescriptor game = registeredGames.get(request.getGameID());
        if(game == null)
            throw new NoSuchGameException(ErrorCode.ERROR_GAME_NOT_FOUND + request.getGameID());

        Player p = new Player(token, request.getPlayerName());
        game.join(p);
        log.info("user " + request.getPlayerName() + " joined game " + game.getId());
    }

    public List<Game> getGames(){
        return registeredGames.values()
                .stream()
                .map(gameDescriptor -> new Game(gameDescriptor.getId(), gameDescriptor.getPlayerCount()))
                .toList();
    }
}
