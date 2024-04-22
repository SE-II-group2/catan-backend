package com.group2.catanbackend.service;

import com.group2.catanbackend.dto.CreateRequestDto;
import com.group2.catanbackend.dto.JoinResponseDto;
import com.group2.catanbackend.dto.LobbyDto;
import com.group2.catanbackend.dto.JoinRequestDto;
import com.group2.catanbackend.dto.game.*;
import com.group2.catanbackend.exception.*;
import com.group2.catanbackend.model.GameDescriptor;
import com.group2.catanbackend.model.Player;
import com.group2.catanbackend.model.PlayerState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Scope("singleton")
public class GameService {
    private final ApplicationContext applicationContext;
    private final MessagingService messagingService;
    @Getter
    private final Map<String, GameDescriptor> registeredGames = new HashMap<>();
    @Getter
    private final Map<String, RunningInstanceService> runningGames = new HashMap<>();
    private final TokenService tokenService;

    @Autowired
    public GameService(ApplicationContext applicationContext,
                       MessagingService messagingService, TokenService tokenService){
        this.applicationContext = applicationContext;
        this.messagingService = messagingService;
        this.tokenService = tokenService;
    }

    public JoinResponseDto createAndJoin(CreateRequestDto requestDto) throws GameException{
        GameDescriptor game = new GameDescriptor();
        String id = game.getId();
        registeredGames.put(id, game);
        log.info("Created game: " + id);
        JoinRequestDto joinRequest = new JoinRequestDto(requestDto.getPlayerName(), id);
        return joinGame(joinRequest);
    }

    public JoinResponseDto joinGame(JoinRequestDto request) throws GameException {
        GameDescriptor game = registeredGames.get(request.getGameID());
        if(game == null)
            throw new NoSuchGameException(ErrorCode.ERROR_GAME_NOT_FOUND + request.getGameID());

        String token = tokenService.generateToken();

        Player p = new Player(token, request.getPlayerName(), game.getId());
        p.setPlayerState(PlayerState.IN_LOBBY);
        game.join(p);

        tokenService.pushToken(token, p);

        PlayerDto playerDto = new PlayerDto(p.getDisplayName(), p.getInGameID(), PlayerState.IN_LOBBY);

        notifyLobbyNewPlayer(game, playerDto);
        log.info("user " + request.getPlayerName() + " joined game " + game.getId());


        return new JoinResponseDto(p.getDisplayName(), p.getGameID(), p.getToken(), p.getInGameID());
    }

    public void startGame(String token){
        Player player = tokenService.getPlayerByToken(token);
        if(player == null)
            throw new NoSuchTokenException(ErrorCode.ERROR_NO_SUCH_TOKEN);

        GameDescriptor game = registeredGames.get(player.getGameID());
        if(game == null)
            throw new NoSuchGameException(ErrorCode.ERROR_GAME_NOT_FOUND.formatted(player.getGameID()));
        if(!game.getAdmin().getToken().equals(token))
            throw new NotAuthorizedException(ErrorCode.ERROR_NOT_AUTHORIZED.formatted("Start Game: Not Admin}"));

        RunningInstanceService service = (RunningInstanceService) applicationContext.getBean("runningInstanceService");
        runningGames.put(game.getId(), service);
        registeredGames.remove(game.getId());
        service.addPlayers(game.getPlayers());
        service.setGameId(game.getId());
        service.start();
    }

    public void leaveGame(String token){
        Player player = tokenService.getPlayerByToken(token);
        if(player == null)
            throw new NoSuchTokenException(ErrorCode.ERROR_NO_SUCH_TOKEN);
        GameDescriptor gameDescriptor = registeredGames.get(player.getGameID());
        RunningInstanceService game = runningGames.get(player.getGameID());

        if(gameDescriptor == null && game == null)
            throw new NoSuchGameException(ErrorCode.ERROR_GAME_NOT_FOUND.formatted(player.getGameID()));

        if(gameDescriptor != null){
            gameDescriptor.leave(player);
            tokenService.revokeToken(token);
            notifyLobbyPlayerLeft(gameDescriptor, player);
            if(gameDescriptor.getPlayerCount() == 0){
                registeredGames.remove(gameDescriptor.getId());
            }
        }
        //As a running Game is a service, the notification is handled by it.
        if(game != null){
            tokenService.revokeToken(token);
            game.removePlayer(player);
        }

    }
    public void makeMove(String token, String gameID, GameMoveDto gameMove){
        Player player = tokenService.getPlayerByToken(token);
        if(player == null)
            throw new NoSuchTokenException(ErrorCode.ERROR_NO_SUCH_TOKEN);

        RunningInstanceService game = runningGames.get(gameID);
        if(game == null)
            throw new NoSuchGameException(ErrorCode.ERROR_GAME_NOT_FOUND.formatted(player.getGameID()));
        game.makeMove(gameMove, player);
        throw new NotImplementedException(ErrorCode.ERROR_NOT_IMPLEMENTED);
    }

    public List<LobbyDto> getLobbies(){
        return registeredGames.values()
                .stream()
                .map(gameDescriptor -> new LobbyDto(gameDescriptor.getId(), gameDescriptor.getPlayerCount()))
                .toList();
    }

    private void notifyLobbyNewPlayer(GameDescriptor gameDescriptor, PlayerDto newPlayer){
        PlayersInLobbyDto data = gameDescriptor.getDtoTemplate();
        data.setEvent(new PlayerEventDto(PlayerEventDto.Type.PLAYER_JOINED, newPlayer));
        messagingService.notifyLobby(gameDescriptor.getId(), data);
    }

    private void notifyLobbyPlayerLeft(GameDescriptor descriptor, Player p){
        PlayersInLobbyDto data = descriptor.getDtoTemplate();
        data.setEvent(new PlayerEventDto(PlayerEventDto.Type.PLAYER_LEFT, p.toPlayerDto()));

        messagingService.notifyLobby(descriptor.getId(), data);
    }
}
