package com.group2.catanbackend.gamelogic;
import com.group2.catanbackend.gamelogic.enums.BuildingType;
import com.group2.catanbackend.gamelogic.objects.Building;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingUnitTest {

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(1, BuildingType.VILLAGE);
        Building building2 = new Building(1, BuildingType.CITY);

        assertEquals(1, building1.getPlayerID());
        assertEquals(1, building2.getPlayerID());
    }
}
