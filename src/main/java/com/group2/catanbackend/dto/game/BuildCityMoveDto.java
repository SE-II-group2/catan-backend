package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuildCityMoveDto extends GameMoveDto {
    public BuildCityMoveDto(int intersectionID) {
        this.intersectionID = intersectionID;
    }
    public int intersectionID;
}

