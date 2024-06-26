package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TradeOfferDto extends MessageDto{
    public TradeOfferDto(int[] tradeMove_getResources, int[] tradeMove_giveResources, IngamePlayerDto fromPlayer) {
        this.getResources=tradeMove_giveResources;
        this.giveResources=tradeMove_getResources;
        this.fromPlayer = fromPlayer;
        this.setEventType(MessageType.TRADE_OFFERED);
    }
    //order swapped!!!
    private int[] getResources;
    private int[] giveResources;
    private IngamePlayerDto fromPlayer;


    public boolean sameAs(TradeOfferDto otherTradeOffer){
        if(this.getResources.length!=otherTradeOffer.getResources.length||this.giveResources.length!=otherTradeOffer.giveResources.length)
            return false;
        for(int i=0;i<getResources.length;i++){
            if(this.getResources[i]!=otherTradeOffer.getResources[i])
                return false;
        }
        for(int i=0;i<giveResources.length;i++){
            if(this.giveResources[i]!=otherTradeOffer.giveResources[i])
                return false;
        }
        if(this.getEventType().equals(otherTradeOffer.getEventType()))
            return false;
        return this.fromPlayer.getInGameID() == otherTradeOffer.fromPlayer.getInGameID();
    }
}
