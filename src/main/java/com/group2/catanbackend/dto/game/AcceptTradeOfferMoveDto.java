package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AcceptTradeOfferMoveDto extends GameMoveDto{
    public AcceptTradeOfferMoveDto(TradeOfferDto tradeOfferDto) {
        this.tradeOfferDto = tradeOfferDto;
        this.setEventType(GameMoveType.ACCEPTRADETMOVE);
    }

    private TradeOfferDto tradeOfferDto;

}

