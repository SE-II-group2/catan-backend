package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AcceptMoveDto extends GameMoveDto{
    public AcceptMoveDto(TradeOfferDto tradeOfferDto) {
        this.tradeOfferDto = tradeOfferDto;
        this.setEventType(GameMoveType.ACCEPTMOVE);
    }

    private TradeOfferDto tradeOfferDto;

}

