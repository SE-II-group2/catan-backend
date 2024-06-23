package com.group2.catanbackend.dto.game;

// fixme see GameMoveType
/**
 * The types of Messages that can be sent.
 * No enum because easier for Jackson
 */

public class MessageType {
  public static final String PLAYERS_CHANGED = "PLAYERS_CHANGED";
  public static final String GAME_STARTED = "GAME_STARTED";
  public static final String GAME_MOVE_NOTIFIER="GAME_MOVE_NOTIFIER";
  public static final String GAME_OBJECT="GAME_OBJECT";
  public static final String INVALID_GAME_MOVE="INVALID_GAME_MOVE";
  public static final String PLAYER_NOTIFY="PLAYER_NOTIFY";
  public static final String GAME_OVER = "GAME_OVER";
}
