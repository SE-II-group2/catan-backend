package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TradeOfferDto extends MessageDto{
    public TradeOfferDto() {
        this.setEventType(MessageType.PLAYER_NOTIFY);
    }
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

}
