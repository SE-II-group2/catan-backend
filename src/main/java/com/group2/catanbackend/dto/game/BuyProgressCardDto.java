package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class BuyProgressCardDto extends GameMoveDto {

    public BuyProgressCardDto() {
        this.setEventType(GameMoveType.BUYPROGRESSCARD);
    }
}