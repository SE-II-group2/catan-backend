package com.group2.catanbackend.dto;

import com.group2.catanbackend.dto.game.MessageDto;
import com.group2.catanbackend.dto.game.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GameMoveValidResponseDto extends MessageDto {
    @Getter
    String message;
     public GameMoveValidResponseDto(String message){
         this.message=message;
     }
}
