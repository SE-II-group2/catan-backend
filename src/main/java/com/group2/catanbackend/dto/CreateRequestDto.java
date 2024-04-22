package com.group2.catanbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequestDto {
    @NotNull(message = "playerName is required")
    @NotEmpty(message = "Player name cannot be Empty")
    private String playerName;
}
