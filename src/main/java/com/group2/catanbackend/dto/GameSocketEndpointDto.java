package com.group2.catanbackend.dto;

import com.group2.catanbackend.config.Constants;
import lombok.Data;

@Data
public class GameSocketEndpointDto {
    private String playerName;
    private String gameID;
    private String token;
    private String serverAddress;
    private int serverPort;
    private String socketURL;
    private String gameProgressPath;
    private String privatePath;

    public GameSocketEndpointDto(String gameID, String playerName, String token){
        this.gameID = gameID;
        this.playerName = playerName;
        this.token = token;
        this.serverAddress = Constants.SOCKET_ADDRESS;
        this.serverPort = Constants.SOCKET_PORT;
        this.socketURL = "ws://" + serverAddress + ":" + serverPort + "/" + Constants.SOCKET_ENDPOINT;
        this.gameProgressPath = "/topic/game/" + gameID + "/game-progress";
        this.privatePath = "/user/queue/message";
    }
}
