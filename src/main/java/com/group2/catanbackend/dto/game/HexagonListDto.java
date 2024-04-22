package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class HexagonListDto {
    @Getter
    private List<HexagonDto> hexagons;
}



