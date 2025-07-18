package com.ruta.api.service;

import com.ruta.api.model.Connection;
import com.ruta.api.model.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CompanyGraphServiceTest {

    @InjectMocks
    private CompanyGraphService companyGraphService;

    private static final String COMPANY_ID = "test-company";

    @BeforeEach
    void setUp() {
        // Clear any existing data
        companyGraphService.clearGraph(COMPANY_ID);
    }

    @Test
    void testGetOrCreateGraph() {
        Graph graph = companyGraphService.getOrCreateGraph(COMPANY_ID);
        
        assertNotNull(graph);
        assertTrue(graph.getLocations().isEmpty());
    }

    @Test
    void testAddConnection() {
        Connection connection = new Connection("A", "B", 10);
        companyGraphService.addConnection(COMPANY_ID, connection);
        
        Graph graph = companyGraphService.getOrCreateGraph(COMPANY_ID);
        Set<String> locations = graph.getLocations();
        
        assertEquals(2, locations.size());
        assertTrue(locations.contains("A"));
        assertTrue(locations.contains("B"));
    }

    @Test
    void testClearGraph() {
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "B", 10));
        companyGraphService.addConnection(COMPANY_ID, new Connection("B", "C", 15));
        
        Graph graph = companyGraphService.getOrCreateGraph(COMPANY_ID);
        assertEquals(3, graph.getLocations().size());
        
        companyGraphService.clearGraph(COMPANY_ID);
        
        graph = companyGraphService.getOrCreateGraph(COMPANY_ID);
        assertEquals(0, graph.getLocations().size());
    }

    @Test
    void testFindShortestRoute() {
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "B", 10));
        companyGraphService.addConnection(COMPANY_ID, new Connection("B", "C", 15));
        companyGraphService.addConnection(COMPANY_ID, new Connection("C", "D", 20));
        
        List<String> route = companyGraphService.findShortestRoute(COMPANY_ID, "A", "D");
        
        assertNotNull(route);
        assertEquals(4, route.size());
        assertEquals("A", route.get(0));
        assertEquals("B", route.get(1));
        assertEquals("C", route.get(2));
        assertEquals("D", route.get(3));
    }

    @Test
    void testFindShortestRouteNoPath() {
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "B", 10));
        companyGraphService.addConnection(COMPANY_ID, new Connection("C", "D", 15));
        
        List<String> route = companyGraphService.findShortestRoute(COMPANY_ID, "A", "D");
        
        assertNull(route);
    }

    @Test
    void testFindShortestRouteNonExistentCompany() {
        List<String> route = companyGraphService.findShortestRoute("non-existent", "A", "B");
        
        assertNull(route);
    }

    @Test
    void testCalculateRouteTime() {
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "B", 10));
        companyGraphService.addConnection(COMPANY_ID, new Connection("B", "C", 15));
        companyGraphService.addConnection(COMPANY_ID, new Connection("C", "D", 20));
        
        List<String> route = List.of("A", "B", "C", "D");
        int totalTime = companyGraphService.calculateRouteTime(COMPANY_ID, route);
        
        assertEquals(45, totalTime); // 10 + 15 + 20
    }

    @Test
    void testCalculateRouteTimeEmptyRoute() {
        List<String> route = List.of();
        int totalTime = companyGraphService.calculateRouteTime(COMPANY_ID, route);
        
        assertEquals(0, totalTime);
    }

    @Test
    void testCalculateRouteTimeSingleLocation() {
        List<String> route = List.of("A");
        int totalTime = companyGraphService.calculateRouteTime(COMPANY_ID, route);
        
        assertEquals(0, totalTime);
    }

    @Test
    void testCalculateRouteTimeNonExistentCompany() {
        List<String> route = List.of("A", "B", "C");
        int totalTime = companyGraphService.calculateRouteTime("non-existent", route);
        
        assertEquals(0, totalTime);
    }

    @Test
    void testMultipleCompanies() {
        String company1 = "company1";
        String company2 = "company2";
        
        companyGraphService.addConnection(company1, new Connection("A", "B", 10));
        companyGraphService.addConnection(company2, new Connection("X", "Y", 20));
        
        Graph graph1 = companyGraphService.getOrCreateGraph(company1);
        Graph graph2 = companyGraphService.getOrCreateGraph(company2);
        
        assertEquals(2, graph1.getLocations().size());
        assertEquals(2, graph2.getLocations().size());
        
        assertTrue(graph1.getLocations().contains("A"));
        assertTrue(graph1.getLocations().contains("B"));
        assertTrue(graph2.getLocations().contains("X"));
        assertTrue(graph2.getLocations().contains("Y"));
    }

    @Test
    void testFindShortestRouteComplexScenario() {
        // Create a complex scenario with multiple paths
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "B", 10));
        companyGraphService.addConnection(COMPANY_ID, new Connection("B", "C", 15));
        companyGraphService.addConnection(COMPANY_ID, new Connection("C", "D", 20));
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "C", 50));
        companyGraphService.addConnection(COMPANY_ID, new Connection("B", "D", 60));
        companyGraphService.addConnection(COMPANY_ID, new Connection("A", "D", 100));
        
        List<String> route = companyGraphService.findShortestRoute(COMPANY_ID, "A", "D");
        
        assertNotNull(route);
        assertEquals(4, route.size());
        assertEquals("A", route.get(0));
        assertEquals("B", route.get(1));
        assertEquals("C", route.get(2));
        assertEquals("D", route.get(3));
        
        int totalTime = companyGraphService.calculateRouteTime(COMPANY_ID, route);
        assertEquals(45, totalTime); // 10 + 15 + 20
    }
} 