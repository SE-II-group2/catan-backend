package com.group2.catanbackend.exception;

public class ErrorCode {
    public static final String ERROR_GAME_NOT_FOUND = "Game does not exist with id:";
    public static final String ERROR_GAME_NOT_IN_LOBBY = "Game is not in lobby. id:";
    public static final String ERROR_PLAYER_ALREADY_IN_GAME = "This Player is already in the game";
    public static final String ERROR_GAME_FULL = "Game already full. id: ";
    public static final String ERROR_NOT_AUTHORIZED = "User is not authorized to perform the specified action: %s";
    public static final String ERROR_NOT_IMPLEMENTED = "This method is not yet implemented";

}
