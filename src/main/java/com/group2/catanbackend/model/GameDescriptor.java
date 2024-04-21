package com.group2.catanbackend.model;

import com.group2.catanbackend.dto.game.PlayerDto;
import com.group2.catanbackend.dto.game.PlayersInLobbyDto;
import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.exception.GameFullException;
import com.group2.catanbackend.exception.PlayerAlreadyInGameException;
import lombok.Getter;

import java.util.*;

public class GameDescriptor {
    @Getter
    private final List<Player> players;
    @Getter
    private final String id;
    @Getter
    private Player admin;
    private final Date createdAt;
    private int nextPlayerID = 1;

    public GameDescriptor(){
        this.players = new LinkedList<>();
        this.id = UUID.randomUUID().toString().substring(0,7);
        this.createdAt = new Date();
    }


    public void join(Player player) throws GameException {
        if(players.contains(player))
            throw new PlayerAlreadyInGameException(ErrorCode.ERROR_PLAYER_ALREADY_IN_GAME);
        if(players.size() >= 4)
            throw new GameFullException(ErrorCode.ERROR_GAME_FULL + id);
        if(players.isEmpty()){
            admin = player;
        }
        players.add(player);
        player.setInGameID(nextPlayerID++);
    }

    public boolean leave(Player player){
        if(players.remove(player)) {
            if (players.isEmpty())
                admin = null;
            else if (player.equals(admin)) {
                admin = players.get(0);
            }
            return true;
        }
        return false;
    }

    public int getPlayerCount(){
        return players.size();
    }

    public PlayersInLobbyDto getDtoTemplate(){
        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setPlayers(getPlayers().stream().map(Player::toPlayerDto).toList());
        dto.setAdmin(getAdmin() != null ? getAdmin().toPlayerDto() : null);
        return dto;
    }
}
