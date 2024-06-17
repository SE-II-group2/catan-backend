package com.group2.catanbackend.dto.game;

import com.group2.catanbackend.gamelogic.enums.ProgressCardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IngamePlayerDto {
    private String displayName;
    private int[] resources;
    private int victoryPoints;
    private int color;
    private int inGameID;
    private List<ProgressCardType> progressCards;
    private boolean connected;
}
