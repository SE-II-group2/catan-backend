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
    public UseProgressCardDto(ProgressCardType progressCardType, List<ResourceDistribution> chosenResources, ResourceDistribution monopolyResource){
        this.progressCardType = progressCardType;
        this.chosenResources = chosenResources;
        this.monopolyResource = monopolyResource;
        //this.robberPosition = robberPosition;
        this.setEventType(GameMoveType.USEPROGRESSCARD);
    }

    // TODO: Implement Robber when ready on Board
    public ProgressCardType progressCardType;
    public List<ResourceDistribution> chosenResources;
    public ResourceDistribution monopolyResource;
    //private Robber position
}
