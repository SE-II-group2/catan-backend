package com.group2.catanbackend.gamelogic;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import com.group2.catanbackend.gamelogic.enums.*;
import com.group2.catanbackend.gamelogic.objects.*;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardUnitTests {

    private Board board;
    private Player player1;
    private Player player2;

    @Mock
    private Building buildingMock;

    @BeforeEach
    public void setUp() {
        player1 = new Player("Token1", "Player One(1)", "this");
        player2 = new Player("Token2", "Player Two(2)", "this");
        board = new Board();
        buildingMock = mock(Building.class); // Create a mock object for Building
    }

    @Test
    public void testGenerateHexagonsSize() {
        assertNotNull(board.getHexagonList());
        assertEquals(19, board.getHexagonList().size()); // Check if 19 hexagons are generated
    }

    @Test
    public void testGenerateHexagonsDistribution() {
        List<Location> locationsWanted = new ArrayList<>();
        List<Integer> valuesWanted = new ArrayList<>();

        Collections.addAll(locationsWanted, Location.HILLS, Location.HILLS, Location.HILLS, Location.FOREST,
                Location.FOREST, Location.FOREST, Location.FOREST, Location.MOUNTAINS, Location.MOUNTAINS,
                Location.MOUNTAINS, Location.FIELDS, Location.FIELDS, Location.FIELDS, Location.FIELDS,
                Location.PASTURE, Location.PASTURE, Location.PASTURE, Location.PASTURE, Location.DESERT);
        Collections.addAll(valuesWanted, 0, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        List<Location> locationsActual = new ArrayList<>();
        List<Integer> valuesActual = new ArrayList<>();

        for (Hexagon hexagon : board.getHexagonList()) {
            locationsActual.add(hexagon.getLocation());
            valuesActual.add(hexagon.getRollValue());
        }

        Collections.sort(locationsActual);
        Collections.sort(locationsWanted);
        Collections.sort(valuesActual);

        assertEquals(locationsWanted, locationsActual);
        assertEquals(valuesWanted, valuesActual);
    }

    @Test
    public void testGenerateHexagonsDesertTileCorrectness() {
        boolean hasDesertTile = false;
        for (Hexagon hexagon : board.getHexagonList()) {
            if (hexagon.getLocation().equals(Location.DESERT)) {
                assertEquals(0, hexagon.getRollValue());
                assertArrayEquals(new int[]{0, 0, 0, 0, 0}, hexagon.getDistribution().getDistribution());
                hasDesertTile = true;
            }
        }
        assertTrue(hasDesertTile);
    }

    @Test
    public void testDistributeResourcesByDiceRoll() {
        for (Hexagon hexagon : board.getHexagonList()) {
            hexagon.addBuilding(buildingMock);
        }
        board.distributeResourcesByDiceRoll(6);

        verify(buildingMock, times(2)).giveResources(any());

    }

    @Test
    public void testAddVillageNormalCase() {
        board.addNewVillage(player1, 2, 5);
        board.addNewVillage(player1, 2, 7);
        board.addNewVillage(player1, 1, 6);
        List<Hexagon> hexList = board.getHexagonList();
        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(2).getNumOfAdjacentBuildings());
        assertEquals(0, hexList.get(3).getNumOfAdjacentBuildings());

        board.addNewVillage(player1, 2, 6);
        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
    }

    @Test
    public void testAddVillageEdgeOfBoard() {
        board.addNewVillage(player1, 2, 0);
        board.addNewVillage(player1, 1, 1);

        List<Hexagon> hexList = board.getHexagonList();
        assertEquals(1, hexList.get(7).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    public void testAddRoad() {
        board.addNewRoad(player1, 0, 1);
        assertTrue(board.isNextToOwnRoad(1, player1));
        assertTrue(board.isNextToOwnRoad(0, player1));
        assertFalse(board.isNextToOwnRoad(8, player1));
    }
}
