package com.group2.catanbackend.dto.game;

public class GameMoveType {
    private GameMoveType(){}
    public static final String BUILDROADMOVE = "BUILD_ROAD_MOVE";
    public static final String BUILDVILLAGEMOVE = "BUILD_VILLAGE_MOVE";
    public static final String BUILDCITYMOVE = "BUILD_CITY_MOVE";
    public static final String ENTTURNMOVE = "END_TURN_MOVE";
    public static final String ROLLDICEMOVE = "ROLL_DICE_MOVE";
    public static final  String USEPROGRESSCARD = "USE_PROGRESS_CARD";
    public static final String BUYPROGRESSCARD = "BUY_PROGRESS_CARD";
    public static final String MOVEROBBERMOVE = "MOVE_ROBBER_MOVE";
    public static final String ACCUSECHEATINGMOVE="ACCUSE_CHEATING_MOVE";
    public static final String TRADEMOVE = "TRADE_MOVE";
    public static final String ACCEPTMOVE = "ACCEPT_MOVE";
}
