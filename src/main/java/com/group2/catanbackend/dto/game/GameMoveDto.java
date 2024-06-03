package com.group2.catanbackend.dto.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// fixme does this work with java oop instead of jackson annotations?
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildRoadMoveDto.class, name = GameMoveType.BUILDROADMOVE),
        @JsonSubTypes.Type(value = BuildVillageMoveDto.class, name = GameMoveType.BUILDVILLAGEMOVE),
        @JsonSubTypes.Type(value = EndTurnMoveDto.class, name = GameMoveType.ENTTURNMOVE),
        @JsonSubTypes.Type(value = RollDiceDto.class, name = GameMoveType.ROLLDICEMOVE),
        @JsonSubTypes.Type(value= MoveRobberDto.class, name = GameMoveType.MOVEROBBERMOVE),
})

//no longer abstract because this causes issues with serialization and deserialization
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameMoveDto {
    private String eventType;
}

