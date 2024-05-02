package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.enums.ResourceDistribution;
import com.group2.catanbackend.gamelogic.objects.Building;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerUnitTests {

    private Player player1;
    @BeforeEach
    public void setUp() {
        player1 = new Player("player1", "player1", "1");
    }

    @Test
    public void testAdjustResources() {
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());

        assertArrayEquals(ResourceDistribution.FOREST.getDistribution(), player1.getResources());
    }

    @Test
    public void testVictoryPoints() {
        player1.increaseVictoryPoints(2);
        assertEquals(2,player1.getVictoryPoints());

        player1.increaseVictoryPoints(-1);
        assertEquals(1,player1.getVictoryPoints());
    }

    @Test
    public void testResourceSufficient() {
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());

        int[] costs1 = new int[]{0, 0, -1, 0, 0}; // -FOREST
        int[] costs2 = new int[]{0, -1, -1, 0, 0};
        assertTrue(player1.resourcesSufficient(costs1));
        assertFalse(player1.resourcesSufficient(costs2));
    }
}
