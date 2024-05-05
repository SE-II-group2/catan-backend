package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntersectionDto {
    private final PlayerDto owner;
    private final String BuildingType;
    private final int id;

}
