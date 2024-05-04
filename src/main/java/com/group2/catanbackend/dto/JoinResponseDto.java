package com.group2.catanbackend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class JoinResponseDto {
    private String playerName;
    private String gameID;
    private String token;
    private int inGameID;
}
