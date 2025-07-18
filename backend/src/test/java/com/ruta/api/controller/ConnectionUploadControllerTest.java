package com.ruta.api.controller;

import com.ruta.api.service.CompanyGraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionUploadControllerTest {

    @Mock
    private CompanyGraphService companyGraphService;

    @InjectMocks
    private ConnectionUploadController controller;

    private static final String COMPANY_ID = "default";

    @BeforeEach
    void setUp() {
        // Reset any existing data
        when(companyGraphService.getOrCreateGraph(COMPANY_ID)).thenReturn(new com.ruta.api.model.Graph());
    }

    @Test
    void testUploadConnectionsCSVSuccess() {
        // Prepare test data
        String csvContent = "origen,destino,tiempo\nA,B,10\nB,C,15\nC,D,20";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        // Execute
        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Connections uploaded successfully", response.getBody());
        
        // Verify service calls
        verify(companyGraphService, times(1)).clearGraph(COMPANY_ID);
        verify(companyGraphService, times(3)).addConnection(eq(COMPANY_ID), any());
    }

    @Test
    void testUploadConnectionsCSVEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "empty.csv", 
            "text/csv", 
            new byte[0]
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty", response.getBody());
        
        verify(companyGraphService, never()).clearGraph(any());
        verify(companyGraphService, never()).addConnection(any(), any());
    }

    @Test
    void testUploadConnectionsCSVInvalidFormat() {
        String csvContent = "origen,destino,tiempo\nA,B\nC,D,15"; // Missing time in second line
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid format at line 2", response.getBody());
    }

    @Test
    void testUploadConnectionsCSVInvalidTime() {
        String csvContent = "origen,destino,tiempo\nA,B,abc\nC,D,15"; // Invalid time
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid time at line 2", response.getBody());
    }

    @Test
    void testUploadConnectionsCSVNegativeTime() {
        String csvContent = "origen,destino,tiempo\nA,B,-10\nC,D,15"; // Negative time
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data at line 2", response.getBody());
    }

    @Test
    void testUploadConnectionsCSVEmptyFields() {
        String csvContent = "origen,destino,tiempo\nA,,10\nC,D,15"; // Empty destination
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data at line 2", response.getBody());
    }

    @Test
    void testUploadConnectionsCSVWithHeader() {
        String csvContent = "origen,destino,tiempo\nA,B,10\nB,C,15";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Connections uploaded successfully", response.getBody());
        
        // Verify that header was skipped and only 2 connections were processed
        verify(companyGraphService, times(1)).clearGraph(COMPANY_ID);
        verify(companyGraphService, times(2)).addConnection(eq(COMPANY_ID), any());
    }

    @Test
    void testUploadConnectionsCSVWithEmptyLines() {
        String csvContent = "origen,destino,tiempo\nA,B,10\n\nB,C,15\n\nC,D,20";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        ResponseEntity<String> response = controller.uploadConnectionsCSV(file, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Connections uploaded successfully", response.getBody());
        
        // Verify that empty lines were skipped and 3 connections were processed
        verify(companyGraphService, times(1)).clearGraph(COMPANY_ID);
        verify(companyGraphService, times(3)).addConnection(eq(COMPANY_ID), any());
    }

    @Test
    void testListConnections() {
        // Mock the graph to return some locations
        com.ruta.api.model.Graph mockGraph = new com.ruta.api.model.Graph();
        mockGraph.addConnection(new com.ruta.api.model.Connection("A", "B", 10));
        mockGraph.addConnection(new com.ruta.api.model.Connection("B", "C", 15));
        
        when(companyGraphService.getOrCreateGraph(COMPANY_ID)).thenReturn(mockGraph);

        ResponseEntity<String> response = controller.listConnections();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Connections for company default"));
        assertTrue(response.getBody().contains("Graph contains 3 locations"));
    }
} 