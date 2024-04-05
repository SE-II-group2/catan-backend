package com.group2.catanbackend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ListGameResponse {
    private int count;
    List<Game> gameList;
}
