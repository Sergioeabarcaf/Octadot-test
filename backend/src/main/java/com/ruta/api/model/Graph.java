package com.ruta.api.model;

import java.util.*;

public class Graph {
    // Map from source location to list of outgoing connections
    private final Map<String, List<Connection>> adjacencyMap = new HashMap<>();

    public void addConnection(Connection connection) {
        adjacencyMap.computeIfAbsent(connection.getSource(), k -> new ArrayList<>()).add(connection);
    }

    public List<Connection> getConnectionsFrom(String source) {
        return adjacencyMap.getOrDefault(source, Collections.emptyList());
    }

    public Set<String> getLocations() {
        Set<String> allLocations = new HashSet<>();
        for (String source : adjacencyMap.keySet()) {
            allLocations.add(source);
            for (Connection connection : adjacencyMap.get(source)) {
                allLocations.add(connection.getTarget());
            }
        }
        return allLocations;
    }

    public void clear() {
        adjacencyMap.clear();
    }

    public Connection getConnection(String from, String to) {
        List<Connection> connections = adjacencyMap.get(from);
        if (connections != null) {
            for (Connection connection : connections) {
                if (connection.getTarget().equals(to)) {
                    return connection;
                }
            }
        }
        return null;
    }

    public List<String> findShortestRoute(String from, String to) {
        Set<String> allLocations = getLocations();
        if (!allLocations.contains(from) || !allLocations.contains(to)) {
            return null;
        }

        // Dijkstra's algorithm
        Map<String, String> previous = new HashMap<>();
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();
        
        // Initialize
        for (String location : allLocations) {
            distances.put(location, Integer.MAX_VALUE);
        }
        distances.put(from, 0);
        
        // Priority queue for Dijkstra
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getDistance));
        queue.add(new Node(from, 0));
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            String currentLocation = current.getLocation();
            
            if (currentLocation.equals(to)) {
                break;
            }
            
            if (visited.contains(currentLocation)) {
                continue;
            }
            visited.add(currentLocation);
            
            for (Connection connection : getConnectionsFrom(currentLocation)) {
                String neighbor = connection.getTarget();
                int newDistance = distances.get(currentLocation) + connection.getTime();
                
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentLocation);
                    queue.add(new Node(neighbor, newDistance));
                }
            }
        }
        
        // Reconstruct path
        if (distances.get(to) == Integer.MAX_VALUE) {
            return null; // No path found
        }
        
        List<String> path = new ArrayList<>();
        String current = to;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        
        return path;
    }

    private static class Node {
        private final String location;
        private final int distance;

        public Node(String location, int distance) {
            this.location = location;
            this.distance = distance;
        }

        public String getLocation() {
            return location;
        }

        public int getDistance() {
            return distance;
        }
    }
} 