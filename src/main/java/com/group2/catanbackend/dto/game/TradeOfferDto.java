package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TradeOfferDto extends MessageDto{
    public TradeOfferDto(int[] tradeMove_getResources, int[] tradeMove_giveResources, int playerID) {
        this.getResources=tradeMove_giveResources;
        this.giveResources=tradeMove_getResources;
        this.playerID = playerID;
        this.setEventType(MessageType.PLAYER_NOTIFY);
    }
    //order swapped!!!
    private int[] getResources;
    private int[] giveResources;
    private int playerID;


    public boolean equals(TradeOfferDto compare){
        if(this.getResources.length!=compare.getResources.length||this.giveResources.length!=compare.giveResources.length)
            return false;
        for(int i=0;i<getResources.length;i++){
            if(this.getResources[i]!=compare.getResources[i])
                return false;
        }
        for(int i=0;i<giveResources.length;i++){
            if(this.giveResources[i]!=compare.giveResources[i])
                return false;
        }
        if(this.getEventType().equals(compare.getEventType()))
            return false;
        return this.getPlayerID() == compare.getPlayerID();
    }
}
