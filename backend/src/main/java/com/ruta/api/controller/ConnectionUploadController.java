package com.ruta.api.controller;

import com.ruta.api.model.Connection;
import com.ruta.api.service.CompanyGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/connections")
public class ConnectionUploadController {

    private static final Logger logger = Logger.getLogger(ConnectionUploadController.class.getName());
    private static final String DEFAULT_COMPANY_ID = "default";

    @Autowired
    private CompanyGraphService companyGraphService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadConnectionsCSV(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        logger.info("Received request to upload CSV");
        String companyId = DEFAULT_COMPANY_ID;
        logger.info("Using default companyId: " + companyId);
        
        if (file.isEmpty()) {
            logger.warning("File is empty");
            return ResponseEntity.badRequest().body("File is empty");
        }
        logger.info("Processing file: " + file.getOriginalFilename());
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            companyGraphService.clearGraph(companyId);
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                
                // Skip header line if it contains non-numeric values
                if (isFirstLine) {
                    try {
                        String[] parts = line.split(",");
                        if (parts.length == 3) {
                            Integer.parseInt(parts[2].trim());
                        }
                    } catch (NumberFormatException e) {
                        // This is a header line, skip it
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }
                
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    logger.warning("Invalid format at line " + lineNumber);
                    return ResponseEntity.badRequest().body("Invalid format at line " + lineNumber);
                }
                String source = parts[0].trim();
                String target = parts[1].trim();
                int time;
                try {
                    time = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    logger.warning("Invalid time at line " + lineNumber);
                    return ResponseEntity.badRequest().body("Invalid time at line " + lineNumber);
                }
                if (source.isEmpty() || target.isEmpty() || time < 0) {
                    logger.warning("Invalid data at line " + lineNumber);
                    return ResponseEntity.badRequest().body("Invalid data at line " + lineNumber);
                }
                companyGraphService.addConnection(companyId, new Connection(source, target, time));
            }
        } catch (IOException e) {
            logger.severe("Error reading file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
        }
        logger.info("Successfully processed " + lineNumber + " lines for companyId: " + companyId);
        return ResponseEntity.ok("Connections uploaded successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<String> listConnections() {
        logger.info("Listing connections for default company");
        String companyId = DEFAULT_COMPANY_ID;
        
        StringBuilder response = new StringBuilder();
        response.append("Connections for company ").append(companyId).append(":\n");
        
        // This is a simple debug endpoint - in a real app you'd return JSON
        // For now, just return a simple text response
        response.append("Graph contains ").append(companyGraphService.getOrCreateGraph(companyId).getLocations().size()).append(" locations\n");
        
        return ResponseEntity.ok(response.toString());
    }
} 