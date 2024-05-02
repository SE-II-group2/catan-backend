package com.group2.catanbackend.dto.game;

/**
 * The types of Messages that can be sent.
 * No enum because easier for Jackson
 */

public interface MessageType {
  String PLAYERS_CHANGED = "PLAYERS_CHANGED";
  String GAME_STARTED = "GAME_STARTED";
  String GAME_MOVE = "GAME_MOVE";
  String GAME_MOVE_NOTIFIER="GAME_MOVE_NOTIFIER";
  String GAME_OBJECT="GAME_OBJECT";
  String INVALID_GAME_MOVE="INVALID_GAME_MOVE";
}
