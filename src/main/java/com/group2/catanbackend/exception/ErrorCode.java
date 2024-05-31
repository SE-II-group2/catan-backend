package com.group2.catanbackend.exception;

public class ErrorCode {
    public static final String ERROR_GAME_NOT_FOUND = "No game with id %s found";
    public static final String ERROR_GAME_NOT_IN_LOBBY = "Game is not in lobby. id:";
    public static final String ERROR_PLAYER_ALREADY_IN_GAME = "This Player is already in the game";
    public static final String ERROR_GAME_FULL = "Game already full. id: ";
    public static final String ERROR_NOT_AUTHORIZED = "User is not authorized to perform the specified action: %s";
    public static final String ERROR_NOT_IMPLEMENTED = "This method is not yet implemented";
    public static final String ERROR_NO_SUCH_TOKEN = "No mapping for this token was found.";
    public static final String ERROR_GAME_ALREADY_OVER = "Game has already finished with winner: %s";
    public static final String ERROR_CANT_ROLL_IN_SETUP = "Cant roll dice during setup phase";
    public static final String ERROR_NOT_ACTIVE_PLAYER = "Not the active player right now. Active Player: %s";
    public static final String ERROR_CANT_BUILD_HERE = "Cant place make %s here!";
    public static final String ERROR_NOT_ENOUGH_RESOURCES = "Not enough Resources to make %s";
    public static final String ERROR_INVALID_DICE_ROLL="Cant roll more than 12 or less than 2";
    public static final String ERROR_DTO_WAS_NULL = "Dto that was transported is null";
    public static final String ERROR_CANT_USE_PROGRESS_CARDS_IN_SETUP = "Progress-cards can't be used during setup phase";
    public static final String ERROR_CARD_TYPE_NOT_IN_POSSESSION = "User doesnt have the progress-card type";
}
