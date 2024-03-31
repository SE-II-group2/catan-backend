package com.group2.catanbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private String token;
    private String displayName;

    @Override
    public int hashCode(){
        return token.hashCode();
    }
}
