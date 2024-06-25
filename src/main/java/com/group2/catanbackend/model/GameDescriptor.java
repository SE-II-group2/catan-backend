package com.group2.catanbackend.model;

import com.group2.catanbackend.config.Constants;
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
    private final boolean[] availableIDs = new boolean[Constants.MAX_PLAYER_COUNT];

    public GameDescriptor(){
        this.players = new LinkedList<>();
        this.id = UUID.randomUUID().toString().substring(0,7);
        availableIDs[0] = false;
        Arrays.fill(availableIDs, true);
    }


    public void join(Player player) throws GameException {
        if(players.contains(player))
            throw new PlayerAlreadyInGameException(ErrorCode.ERROR_PLAYER_ALREADY_IN_GAME);
        if(players.size() >= Constants.MAX_PLAYER_COUNT)
            throw new GameFullException(ErrorCode.ERROR_GAME_FULL + id);
        if(players.isEmpty()){
            admin = player;
        }
        players.add(player);
        player.setInGameID(useNextGameID());
    }

    public boolean leave(Player player){
        if(players.remove(player)) {
            availableIDs[player.getInGameID()] = true;
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
    private int getNextPlayerID(){
        for(int i = 0; i < Constants.MAX_PLAYER_COUNT; i++){
            if(availableIDs[i])
                return i;
        }
        return -1;
    }
    private int useNextGameID(){
        int next = getNextPlayerID();
        availableIDs[next] = false;
        return next;
    }

    public PlayersInLobbyDto getPlayersInLobbyDto(){
        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setPlayers(getPlayers().stream().map(Player::toPlayerDto).toList());
        dto.setAdmin(getAdmin() != null ? getAdmin().toPlayerDto() : null);
        return dto;
    }
}
