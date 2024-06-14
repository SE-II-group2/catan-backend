package com.group2.catanbackend.model;

public enum PlayerState {
    SOFT_JOINED, //Meaning: The player has obtained a token and a space in the lobby is reserved. No connection to the socket has been established.
    CONNECTED, //Meaning: The player is connected to the game socket and ready to receive messages.
    PLAYING, //not needed at the moment. Maybe for indicating that it's the players turn
    DISCONNECTED, //The player has lost connection, but the token has not yet been revoked
}
