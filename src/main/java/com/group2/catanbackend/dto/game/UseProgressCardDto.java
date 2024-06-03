package com.group2.catanbackend.dto.game;

import com.group2.catanbackend.gamelogic.enums.ProgressCardType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UseProgressCardDto extends GameMoveDto {

    // TODO: Implement Robber when ready on Board

    private ProgressCardType progressCardType;
    private List<ResourceDistribution> chosenResources;
    private ResourceDistribution monopolyResource;
    //private Robber position

    public UseProgressCardDto(ProgressCardType progressCardType, List<ResourceDistribution> chosenResources, ResourceDistribution monopolyResource){
        this.progressCardType = progressCardType;
        this.chosenResources = chosenResources;
        this.monopolyResource = monopolyResource;
        //this.robberPosition = robberPosition;
        this.setEventType(GameMoveType.USEPROGRESSCARD);
    }
}