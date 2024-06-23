package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.gamelogic.enums.*;
import com.group2.catanbackend.gamelogic.objects.*;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class HexagonUnitTest {
    private Player player1;
    @BeforeEach
    void setUp() {
        player1 = new Player("player1", "player1", "1");
    }
    @Test
    void testAddBuilding() {
        Hexagon hexagon = new Hexagon(HexagonType.FOREST, ResourceDistribution.FOREST, 6, 1, false);
        Building building1 = new Building(player1, BuildingType.VILLAGE, 1);
        hexagon.addBuilding(building1);

        assertEquals(1, hexagon.getNumOfAdjacentBuildings());
        assertEquals(building1, hexagon.getBuildings()[0]);
    }

    @Mock
    private Building buildingMock;

    @Test
    void testDistributeResources() {
        // Create a mock object for Building

        buildingMock = mock(Building.class);

        Hexagon hexagon = new Hexagon(HexagonType.HILLS, ResourceDistribution.HILLS, 4, 1, false);
        hexagon.addBuilding(buildingMock);

        // Assume giveResources method properly modifies resources for Buildings
        hexagon.distributeResources();

        verify(buildingMock, times(1)).giveResources(hexagon.getDistribution());
    }
}
