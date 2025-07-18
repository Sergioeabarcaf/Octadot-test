package com.ruta.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {

    @Test
    void testConnectionCreation() {
        Connection connection = new Connection("A", "B", 10);
        
        assertEquals("A", connection.getSource());
        assertEquals("B", connection.getTarget());
        assertEquals(10, connection.getTime());
    }

    @Test
    void testConnectionWithZeroTime() {
        Connection connection = new Connection("X", "Y", 0);
        
        assertEquals("X", connection.getSource());
        assertEquals("Y", connection.getTarget());
        assertEquals(0, connection.getTime());
    }

    @Test
    void testConnectionWithLargeTime() {
        Connection connection = new Connection("Start", "End", 999);
        
        assertEquals("Start", connection.getSource());
        assertEquals("End", connection.getTarget());
        assertEquals(999, connection.getTime());
    }

    @Test
    void testConnectionEquality() {
        Connection connection1 = new Connection("A", "B", 10);
        Connection connection2 = new Connection("A", "B", 10);
        Connection connection3 = new Connection("A", "B", 20);
        
        assertEquals(connection1, connection2);
        assertNotEquals(connection1, connection3);
    }
} 