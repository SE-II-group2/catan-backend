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

class BoardUnitTests {

    private Board board;
    private Player player1;

    @Mock
    private Building buildingMock;

    @BeforeEach
    void setUp() {
        player1 = new Player("Token1", "Player One(1)", "this");
        Player player2 = new Player("Token2", "Player Two(2)", "this");
        board = new Board();
        buildingMock = mock(Building.class); // Create a mock object for Building
    }

    @Test
    void testGenerateHexagonsSize() {
        assertNotNull(board.getHexagonList());
        assertEquals(19, board.getHexagonList().size()); // Check if 19 hexagons are generated
    }

    @Test
    void testGenerateHexagonsDistribution() {
        List<HexagonType> locationsWanted = new ArrayList<>();
        List<Integer> valuesWanted = new ArrayList<>();

        Collections.addAll(locationsWanted, HexagonType.HILLS, HexagonType.HILLS, HexagonType.HILLS, HexagonType.FOREST,
                HexagonType.FOREST, HexagonType.FOREST, HexagonType.FOREST, HexagonType.MOUNTAINS, HexagonType.MOUNTAINS,
                HexagonType.MOUNTAINS, HexagonType.FIELDS, HexagonType.FIELDS, HexagonType.FIELDS, HexagonType.FIELDS,
                HexagonType.PASTURE, HexagonType.PASTURE, HexagonType.PASTURE, HexagonType.PASTURE, HexagonType.DESERT);
        Collections.addAll(valuesWanted, 0, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        List<HexagonType> locationsActual = new ArrayList<>();
        List<Integer> valuesActual = new ArrayList<>();

        for (Hexagon hexagon : board.getHexagonList()) {
            locationsActual.add(hexagon.getHexagonType());
            valuesActual.add(hexagon.getRollValue());
        }

        Collections.sort(locationsActual);
        Collections.sort(locationsWanted);
        Collections.sort(valuesActual);

        assertEquals(locationsWanted, locationsActual);
        assertEquals(valuesWanted, valuesActual);
    }

    @Test
    void testGenerateHexagonsDesertTileCorrectness() {
        boolean hasDesertTile = false;
        for (Hexagon hexagon : board.getHexagonList()) {
            if (hexagon.getHexagonType().equals(HexagonType.DESERT)) {
                assertEquals(0, hexagon.getRollValue());
                assertArrayEquals(new int[]{0, 0, 0, 0, 0}, hexagon.getDistribution().getDistribution());
                hasDesertTile = true;
            }
        }
        assertTrue(hasDesertTile);
    }

    @Test
    void testDistributeResourcesByDiceRoll() {
        for (Hexagon hexagon : board.getHexagonList()) {
            hexagon.addBuilding(buildingMock);
        }
        board.distributeResourcesByDiceRoll(6);

        verify(buildingMock, times(2)).giveResources(any());
    }

    @Test
    void testAddVillageNormalCase() {
        board.addNewRoad(player1,14);
        board.addNewRoad(player1,28);
        board.addNewRoad(player1,29);

        board.addNewVillage(player1, 21);
        board.addNewVillage(player1, 23);
        assertFalse(board.addNewVillage(player1, 22));
        board.addNewRoad(player1,14);
        board.addNewRoad(player1,28);
        board.addNewRoad(player1,29);

        board.addNewVillage(player1, 21);
        board.addNewVillage(player1, 23);
        assertFalse(board.addNewVillage(player1, 22));
        board.addNewVillage(player1, 12);

        List<Hexagon> hexList = board.getHexagonList();

        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(2).getNumOfAdjacentBuildings());
        assertEquals(0, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    void testAddVillageEdgeOfBoard() {
        board.addNewRoad(player1,18);
        board.addNewRoad(player1,23);

        board.addNewVillage(player1, 16);
        board.addNewVillage(player1, 7);

        List<Hexagon> hexList = board.getHexagonList();
        assertEquals(1, hexList.get(7).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    void testAddVillageSetupUpPhase(){
        assertTrue(board.addNewVillage(player1, 16));
        assertTrue(board.addNewRoad(player1,23));

        board.setSetupPhase(false);

        assertFalse(board.addNewVillage(player1,7));
        assertTrue(board.addNewRoad(player1,18));
        assertTrue(board.addNewVillage(player1,7));
    }

    @Test
    void testAddCityToVillage() {
        board.addNewVillage(player1,19);
        board.addNewRoad(player1,26);
        board.addNewRoad(player1,27);
        assertSame(BuildingType.EMPTY,board.getIntersections()[2][5].getType());
        board.setSetupPhase(false);

        board.addNewVillage(player1,21);
        assertSame(BuildingType.VILLAGE, board.getIntersections()[2][5].getType());

        board.addNewCity(player1, 21);
        assertSame(BuildingType.CITY, board.getIntersections()[2][5].getType());
    }

    @Test
    void testAddCityNormalCase() {
        board.addNewRoad(player1,14);
        board.addNewRoad(player1,28);
        board.addNewRoad(player1,29);
        board.addNewVillage(player1, 21);
        board.addNewVillage(player1, 23);
        board.addNewVillage(player1, 12);
        board.addNewCity(player1, 21);
        board.addNewCity(player1, 23);
        board.addNewCity(player1, 12);

        List<Hexagon> hexList = board.getHexagonList();

        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(2).getNumOfAdjacentBuildings());
        assertEquals(0, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    void testAddRoad() {
        board.addNewVillage(player1,0);
        assertTrue(board.addNewRoad(player1, 0));
        assertTrue(board.getAdjacencyMatrix()[1][2].isNextToOwnRoad(board,player1,1));
    }

    @Test
    void testAddRoadInvalidPlacement(){
        board.addNewVillage(player1,0);
        assertTrue(board.addNewRoad(player1, 0));
        assertFalse(board.addNewRoad(player1, 0));

        board.setSetupPhase(false);

        assertTrue(board.addNewRoad(player1,1));
        assertFalse(board.addNewRoad(player1,3));
    }

    @Test
    void testAddVillageNextToVillage(){
        board.addNewRoad(player1,0);
        board.addNewRoad(player1,6);
        board.addNewRoad(player1,1);
        board.addNewRoad(player1,2);
        board.addNewRoad(player1,7);
        board.addNewRoad(player1,13);

        assertTrue(board.addNewVillage(player1, 0));
        assertFalse(board.addNewVillage(player1, 8));
        assertTrue(board.addNewVillage(player1, 3));
        assertTrue(board.addNewVillage(player1, 11));
    }

}
