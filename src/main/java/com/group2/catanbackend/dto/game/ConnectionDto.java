package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectionDto {
    private PlayerDto owner;
    private final int id;
}
