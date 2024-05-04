package com.group2.catanbackend.dto.game;

import com.group2.catanbackend.model.PlayerState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Describes a Player only with data that can safely be published. For use in frontend.
 */
public class PlayerDto {
    private String displayName;
    private int inGameID;
    private PlayerState state;
}
