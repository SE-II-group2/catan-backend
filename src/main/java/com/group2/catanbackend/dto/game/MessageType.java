package com.group2.catanbackend.dto.game;

/**
 * The types of Messages that can be sent.
 * No enum because easier for Jackson
 */

public interface MessageType {
  String PLAYERS_CHANGED = "PLAYERS_CHANGED";
  String GAME_STARTED = "GAME_STARTED";
}
