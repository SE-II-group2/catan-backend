package com.group2.catanbackend.config;

public class Constants {
    public static final String SOCKET_ENDPOINT = "catan";
    public static final String SOCKET_ADDRESS = "localhost";
    public static final int SOCKET_PORT = 8080;

    public static final String TOPIC_GAME = "/topic/game/%s/";
    public static final String TOPIC_GAME_PROGRESS = "/topic/game/%s/game-progress";
    public static final String TOPIC_GAME_LOBBY = "/topic/game/%s/messages";
    public static final String QUEUE_USER_MESSAGE = "/queue/messages";
    public static final String USER_DESTINATION_PREFIX = "/user";
    public static final String FULL_USER_QUEUE_PATH = USER_DESTINATION_PREFIX+QUEUE_USER_MESSAGE;
    public static final int MAX_PLAYER_COUNT = 4;
}
