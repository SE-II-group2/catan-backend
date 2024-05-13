package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingUnitTest {
    private Player player1;
    @BeforeEach
    public void setUp() {
        player1 = new Player("player1", "player1", "1");
    }

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(player1, BuildingType.VILLAGE, 1);
        Building building2 = new Building(player1, BuildingType.CITY, 2);

        assertEquals(player1, building1.getPlayer());
        assertEquals(player1, building2.getPlayer());
    }
}
