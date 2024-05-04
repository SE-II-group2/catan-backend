package com.group2.catanbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JoinRequestDto{
    @NotNull
    @NotEmpty
    private String playerName;
    @NotNull
    @NotEmpty
    private String gameID;
}
