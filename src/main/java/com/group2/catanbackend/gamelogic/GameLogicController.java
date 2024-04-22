package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.service.MessagingService;
import com.group2.catanbackend.service.RunningInstanceService;
import lombok.Getter;

import java.util.List;

public class GameLogicController {
    @Getter
    private Board board;
    @Getter
    private List<Player> players;
    private RunningInstanceService runningInstanceService;
    @Getter
    private String gameId;

    public GameLogicController(List<Player> players, RunningInstanceService runningInstanceService, String gameId) {
        this.players = players;
        this.runningInstanceService = runningInstanceService;
        this.gameId = gameId;

        board = new Board(players.size());
    }
}
