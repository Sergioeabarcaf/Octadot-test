package com.ruta.api.controller;

import com.ruta.api.service.CompanyGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private static final Logger logger = Logger.getLogger(RouteController.class.getName());
    private static final String DEFAULT_COMPANY_ID = "default";

    @Autowired
    private CompanyGraphService companyGraphService;

    @GetMapping("/shortest")
    public ResponseEntity<?> findShortestRoute(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        
        logger.info("Finding shortest route from " + from + " to " + to);
        
        try {
            List<String> route = companyGraphService.findShortestRoute(DEFAULT_COMPANY_ID, from, to);
            if (route == null || route.isEmpty()) {
                return ResponseEntity.badRequest().body("No route found between " + from + " and " + to);
            }
            
            int totalTime = companyGraphService.calculateRouteTime(DEFAULT_COMPANY_ID, route);
            
            return ResponseEntity.ok(new RouteResponse(route, totalTime));
        } catch (Exception e) {
            logger.severe("Error finding route: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error finding route: " + e.getMessage());
        }
    }

    public static class RouteResponse {
        private List<String> route;
        private int totalTime;

        public RouteResponse(List<String> route, int totalTime) {
            this.route = route;
            this.totalTime = totalTime;
        }

        public List<String> getRoute() {
            return route;
        }

        public int getTotalTime() {
            return totalTime;
        }
    }
} 