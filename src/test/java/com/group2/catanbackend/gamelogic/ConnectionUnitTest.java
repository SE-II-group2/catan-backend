package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.gamelogic.objects.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionUnitTest {

    @Test
    public void testRoadOwner() {
        Connection connection = new Road(1);
        assertEquals(1, connection.getPlayerID());
    }
}
