package com.group2.catanbackend.gamelogic;

import com.group2.catanbackend.gamelogic.objects.*;
import com.group2.catanbackend.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionUnitTest {
    private Player player1;
    @BeforeEach
    public void setUp() {
        player1 = new Player("player1", "player1", "1");
    }

    @Test
    public void testRoadOwner() {
        Connection connection = new Road(player1, 1);
        assertEquals(player1, connection.getPlayer());
    }
}
