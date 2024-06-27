package com.group2.catanbackend.dto.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildRoadMoveDto.class, name = GameMoveType.BUILDROADMOVE),
        @JsonSubTypes.Type(value = BuildVillageMoveDto.class, name = GameMoveType.BUILDVILLAGEMOVE),
        @JsonSubTypes.Type(value = BuildCityMoveDto.class, name = GameMoveType.BUILDCITYMOVE),
        @JsonSubTypes.Type(value = EndTurnMoveDto.class, name = GameMoveType.ENTTURNMOVE),
        @JsonSubTypes.Type(value = RollDiceDto.class, name = GameMoveType.ROLLDICEMOVE),
        @JsonSubTypes.Type(value = BuyProgressCardDto.class, name = GameMoveType.BUYPROGRESSCARD),
        @JsonSubTypes.Type(value = UseProgressCardDto.class, name = GameMoveType.USEPROGRESSCARD),
        @JsonSubTypes.Type(value= MoveRobberDto.class, name = GameMoveType.MOVEROBBERMOVE),
        @JsonSubTypes.Type(value = AccuseCheatingDto.class, name = GameMoveType.ACCUSECHEATINGMOVE),
        @JsonSubTypes.Type(value = MakeTradeOfferMoveDto.class, name = GameMoveType.MAKETRADEMOVE),
        @JsonSubTypes.Type(value = AcceptTradeOfferMoveDto.class, name = GameMoveType.ACCEPTTRADEMOVE),
})

//no longer abstract because this causes issues with serialization and deserialization
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameMoveDto {
    private String eventType;
}

