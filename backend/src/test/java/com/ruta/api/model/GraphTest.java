package com.ruta.api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

class GraphTest {

    private Graph graph;

    @BeforeEach
    void setUp() {
        graph = new Graph();
    }

    @Test
    void testAddConnection() {
        Connection connection = new Connection("A", "B", 10);
        graph.addConnection(connection);
        
        List<Connection> connections = graph.getConnectionsFrom("A");
        assertEquals(1, connections.size());
        assertEquals(connection, connections.get(0));
    }

    @Test
    void testGetConnectionsFromEmptyLocation() {
        List<Connection> connections = graph.getConnectionsFrom("NonExistent");
        assertTrue(connections.isEmpty());
    }

    @Test
    void testGetLocations() {
        graph.addConnection(new Connection("A", "B", 10));
        graph.addConnection(new Connection("B", "C", 15));
        graph.addConnection(new Connection("C", "D", 20));
        
        Set<String> locations = graph.getLocations();
        assertEquals(4, locations.size());
        assertTrue(locations.contains("A"));
        assertTrue(locations.contains("B"));
        assertTrue(locations.contains("C"));
        assertTrue(locations.contains("D"));
    }

    @Test
    void testClearGraph() {
        graph.addConnection(new Connection("A", "B", 10));
        graph.addConnection(new Connection("B", "C", 15));
        
        assertEquals(3, graph.getLocations().size());
        
        graph.clear();
        
        assertEquals(0, graph.getLocations().size());
        assertTrue(graph.getConnectionsFrom("A").isEmpty());
    }

    @Test
    void testGetConnection() {
        Connection connection = new Connection("A", "B", 10);
        graph.addConnection(connection);
        
        Connection found = graph.getConnection("A", "B");
        assertEquals(connection, found);
        
        Connection notFound = graph.getConnection("A", "C");
        assertNull(notFound);
    }

    @Test
    void testFindShortestRouteDirect() {
        graph.addConnection(new Connection("A", "B", 10));
        
        List<String> route = graph.findShortestRoute("A", "B");
        
        assertNotNull(route);
        assertEquals(2, route.size());
        assertEquals("A", route.get(0));
        assertEquals("B", route.get(1));
    }

    @Test
    void testFindShortestRouteIndirect() {
        graph.addConnection(new Connection("A", "B", 10));
        graph.addConnection(new Connection("B", "C", 15));
        graph.addConnection(new Connection("A", "C", 50)); // Longer direct route
        
        List<String> route = graph.findShortestRoute("A", "C");
        
        assertNotNull(route);
        assertEquals(3, route.size());
        assertEquals("A", route.get(0));
        assertEquals("B", route.get(1));
        assertEquals("C", route.get(2));
    }

    @Test
    void testFindShortestRouteNoPath() {
        graph.addConnection(new Connection("A", "B", 10));
        graph.addConnection(new Connection("C", "D", 15));
        
        List<String> route = graph.findShortestRoute("A", "D");
        
        assertNull(route);
    }

    @Test
    void testFindShortestRouteSameLocation() {
        graph.addConnection(new Connection("A", "B", 10));
        
        List<String> route = graph.findShortestRoute("A", "A");
        
        assertNotNull(route);
        assertEquals(1, route.size());
        assertEquals("A", route.get(0));
    }

    @Test
    void testFindShortestRouteComplex() {
        // Create a complex graph
        graph.addConnection(new Connection("A", "B", 10));
        graph.addConnection(new Connection("B", "C", 15));
        graph.addConnection(new Connection("C", "D", 20));
        graph.addConnection(new Connection("A", "C", 50));
        graph.addConnection(new Connection("B", "D", 60));
        graph.addConnection(new Connection("A", "D", 100));
        
        List<String> route = graph.findShortestRoute("A", "D");
        
        assertNotNull(route);
        assertEquals(4, route.size());
        assertEquals("A", route.get(0));
        assertEquals("B", route.get(1));
        assertEquals("C", route.get(2));
        assertEquals("D", route.get(3));
    }

    @Test
    void testFindShortestRouteWithNonExistentLocations() {
        graph.addConnection(new Connection("A", "B", 10));
        
        List<String> route = graph.findShortestRoute("X", "Y");
        
        assertNull(route);
    }
} 