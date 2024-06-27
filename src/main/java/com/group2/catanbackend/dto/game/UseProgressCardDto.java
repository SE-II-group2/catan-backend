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
    public UseProgressCardDto(ProgressCardType progressCardType, List<ResourceDistribution> chosenResources, ResourceDistribution monopolyResource, int hexagonID){
        this.progressCardType = progressCardType;
        this.chosenResources = chosenResources;
        this.monopolyResource = monopolyResource;
        this.hexagonID = hexagonID;
    }
    private ProgressCardType progressCardType;
    private List<ResourceDistribution> chosenResources;
    private ResourceDistribution monopolyResource;
    private int hexagonID;
}
