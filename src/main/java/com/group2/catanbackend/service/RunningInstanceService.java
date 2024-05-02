package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.exception.InvalidGameMoveException;
import com.group2.catanbackend.exception.NoSuchGameException;
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
    @Getter
    @Setter
    private String gameId;
    private List<Player> players;
    private final MessagingService messagingService;
    private boolean started = false;
    private GameLogicController gameLogicController;

    @Autowired
    public RunningInstanceService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public void makeMove(GameMoveDto gameMove, Player player) {
        if (gameMove == null)
            throw new InvalidGameMoveException(ErrorCode.ERROR_DTO_WAS_NULL);

        if (gameLogicController != null) gameLogicController.makeMove(gameMove, player);
        else throw new NoSuchGameException(ErrorCode.ERROR_GAME_NOT_FOUND);
    }

    public void addPlayers(List<Player> players) {
        this.players = players;
        players.forEach(player -> player.setPlayerState(PlayerState.PLAYING));
    }

    public void start() {
        if (started) {
            return;
        }
        notifyGameStart();
        started = true;
        gameLogicController = new GameLogicController(players, messagingService, gameId);
    }

    //Players are not removed once the game is started.
    public void removePlayer(Player p) {
        p.setPlayerState(PlayerState.DISCONNECTED);
        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setPlayers(players.stream().map(Player::toPlayerDto).toList());
        dto.setAdmin(null);
        dto.setEvent(new PlayerEventDto(PlayerEventDto.Type.PLAYER_LEFT, p.toPlayerDto()));


        messagingService.notifyLobby(gameId, dto);
    }

    public void notifyGameStart() {
        GameStartedDto dto = new GameStartedDto();
        messagingService.notifyLobby(gameId, dto);
    }
}
