package com.group2.catanbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class LobbyDto {
    private String gameID;
    private int playerCount;
}
