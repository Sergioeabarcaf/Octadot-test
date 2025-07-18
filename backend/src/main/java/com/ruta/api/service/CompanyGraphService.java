package com.ruta.api.service;

import com.ruta.api.model.Connection;
import com.ruta.api.model.Graph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompanyGraphService {
    // Map from companyId to its graph
    private final Map<String, Graph> graphsByCompany = new ConcurrentHashMap<>();

    public Graph getOrCreateGraph(String companyId) {
        return graphsByCompany.computeIfAbsent(companyId, k -> new Graph());
    }

    public void clearGraph(String companyId) {
        Graph graph = graphsByCompany.get(companyId);
        if (graph != null) {
            graph.clear();
        }
    }

    public void addConnection(String companyId, Connection connection) {
        getOrCreateGraph(companyId).addConnection(connection);
    }

    public List<String> findShortestRoute(String companyId, String from, String to) {
        Graph graph = graphsByCompany.get(companyId);
        if (graph == null) {
            return null;
        }
        return graph.findShortestRoute(from, to);
    }

    public int calculateRouteTime(String companyId, List<String> route) {
        Graph graph = graphsByCompany.get(companyId);
        if (graph == null || route == null || route.size() < 2) {
            return 0;
        }
        
        int totalTime = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String current = route.get(i);
            String next = route.get(i + 1);
            Connection connection = graph.getConnection(current, next);
            if (connection != null) {
                totalTime += connection.getTime();
            }
        }
        return totalTime;
    }
} 