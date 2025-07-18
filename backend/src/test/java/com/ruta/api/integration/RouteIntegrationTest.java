package com.ruta.api.integration;

import com.ruta.api.service.CompanyGraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebMvc
class RouteIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CompanyGraphService companyGraphService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Clear any existing data
        companyGraphService.clearGraph("default");
    }

    @Test
    void testCompleteFlow() throws Exception {
        // Step 1: Upload CSV file
        String csvContent = "origen,destino,tiempo\nA,B,10\nB,C,15\nC,D,20\nA,C,50\nB,D,60\nA,D,100";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        mockMvc.perform(multipart("/api/connections/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Connections uploaded successfully"));

        // Step 2: Find shortest route
        mockMvc.perform(get("/api/routes/shortest")
                .param("from", "A")
                .param("to", "D")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route").isArray())
                .andExpect(jsonPath("$.route[0]").value("A"))
                .andExpect(jsonPath("$.route[1]").value("B"))
                .andExpect(jsonPath("$.route[2]").value("C"))
                .andExpect(jsonPath("$.route[3]").value("D"))
                .andExpect(jsonPath("$.totalTime").value(45));

        // Step 3: Test direct route
        mockMvc.perform(get("/api/routes/shortest")
                .param("from", "A")
                .param("to", "B")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route").isArray())
                .andExpect(jsonPath("$.route[0]").value("A"))
                .andExpect(jsonPath("$.route[1]").value("B"))
                .andExpect(jsonPath("$.totalTime").value(10));
    }

    @Test
    void testUploadInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            "origen,destino,tiempo\nA,B,abc".getBytes()
        );

        mockMvc.perform(multipart("/api/connections/upload")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid time at line 2"));
    }

    @Test
    void testFindRouteWithoutData() throws Exception {
        mockMvc.perform(get("/api/routes/shortest")
                .param("from", "A")
                .param("to", "B")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No route found between A and B"));
    }

    @Test
    void testListConnections() throws Exception {
        // First upload some data
        String csvContent = "origen,destino,tiempo\nA,B,10\nB,C,15";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.csv", 
            "text/csv", 
            csvContent.getBytes()
        );

        mockMvc.perform(multipart("/api/connections/upload")
                .file(file))
                .andExpect(status().isOk());

        // Then test the list endpoint
        mockMvc.perform(get("/api/connections/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Connections for company default")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Graph contains 3 locations")));
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "empty.csv", 
            "text/csv", 
            new byte[0]
        );

        mockMvc.perform(multipart("/api/connections/upload")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is empty"));
    }

    @Test
    void testUploadFileWithInvalidFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.csv", 
            "text/csv", 
            "origen,destino,tiempo\nA,B\nC,D,15".getBytes()
        );

        mockMvc.perform(multipart("/api/connections/upload")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid format at line 2"));
    }

    @Test
    void testPerformanceWithLargeDataset() throws Exception {
        // Create a large dataset
        StringBuilder csvContent = new StringBuilder("origen,destino,tiempo\n");
        for (int i = 0; i < 100; i++) {
            csvContent.append("A").append(i).append(",B").append(i).append(",").append(i + 1).append("\n");
        }
        csvContent.append("A0,B99,50\n"); // Create a path from A0 to B99

        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "large.csv", 
            "text/csv", 
            csvContent.toString().getBytes()
        );

        // Upload should complete quickly
        long startTime = System.currentTimeMillis();
        mockMvc.perform(multipart("/api/connections/upload")
                .file(file))
                .andExpect(status().isOk());
        long uploadTime = System.currentTimeMillis() - startTime;

        // Route calculation should complete in less than 300ms
        startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/routes/shortest")
                .param("from", "A0")
                .param("to", "B99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        long routeTime = System.currentTimeMillis() - startTime;

        // Verify performance requirements
        assertTrue(uploadTime < 1000, "Upload should complete in less than 1 second");
        assertTrue(routeTime < 300, "Route calculation should complete in less than 300ms");
    }
} 