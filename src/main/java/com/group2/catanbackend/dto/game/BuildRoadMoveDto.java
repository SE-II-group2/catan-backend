package com.group2.catanbackend.dto.game;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class BuildRoadMoveDto extends GameMoveDto{
    @NotNull
    @NotEmpty
    private int connectionID;
}

