package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.NotImplementedException;
import com.group2.catanbackend.gamelogic.GameLogicController;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.model.PlayerState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class RunningInstanceService {
    @Getter @Setter
    private String gameId;
    private List<Player> players;
    private final MessagingService messagingService;
    private boolean started = false;
    private GameLogicController gameLogicController;

    @Autowired
    public RunningInstanceService(MessagingService messagingService){
        this.messagingService = messagingService;
    }

    public void makeMove(Object gameMove)  {
        //TODO: Implement
        throw new NotImplementedException(ErrorCode.ERROR_NOT_IMPLEMENTED);
    }

    public void addPlayers(List<Player> players){
        this.players = players;
        players.forEach(player -> player.setPlayerState(PlayerState.PLAYING));
    }

    public void start(){
        if(started){
            return;
        }
        notifyGameStart();
        started = true;
        gameLogicController = new GameLogicController(players, this, gameId);
    }

    //Players are not removed once the game is started.
    public void removePlayer(Player p){
        p.setPlayerState(PlayerState.DISCONNECTED);
        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setPlayers(players.stream().map(Player::toPlayerDto).toList());
        dto.setAdmin(null);
        dto.setEvent(new PlayerEventDto(PlayerEventDto.Type.PLAYER_LEFT, p.toPlayerDto()));


        messagingService.notifyLobby(gameId, dto);
    }

    public void notifyGameStart(){
        GameStartedDto dto = new GameStartedDto();
        //maybe add the initialized gameBoard here?
        messagingService.notifyLobby(gameId, dto);
    }

    //TODO: Bootstrap game. Create board etc.
}
