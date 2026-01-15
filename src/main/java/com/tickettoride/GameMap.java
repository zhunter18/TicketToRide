package com.tickettoride;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * GameMap class - handles the map implementation for Ticket to Ride
 */
public class GameMap {
    
    /**
     * WeightedGraph class - represents a graph with weighted edges
     */
    protected class WeightedGraph {
        protected Map<String, List<Edge>> adjacencyList;
        protected Set<String> cities;

        /**
         * Color enum for Ticket to Ride route colors
         * null represents multicolor/wildcard routes
         */
        

        /**
         * Edge class represents a connection between two cities
         * Stores destination, weight (distance), tunnel status, ferry count, color, and claim state
         */
        protected class Edge {
            private String destination;
            private int weight;
            private boolean isTunnel;
            private int ferryCount;
            private Color color;  // null = multicolor/wildcard
            private String claimedBy;  // null = unclaimed

            public Edge(String destination, int weight, boolean isTunnel, int ferryCount, Color color) {
                // Validate ferryCount is less than weight
                if (ferryCount >= weight) {
                    throw new IllegalArgumentException("Ferry count must be less than weight");
                }
                this.destination = destination;
                this.weight = weight;
                this.isTunnel = isTunnel;
                this.ferryCount = ferryCount;
                this.color = color;  // null is valid (multicolor)
                this.claimedBy = null;  // Initially unclaimed
            }

            /**
             * Accessor methods for immutable properties
             */
            public String getDestination() {
                return destination;
            }

            public int getWeight() {
                return weight;
            }

            public boolean isTunnel() {
                return isTunnel;
            }

            public int getFerryCount() {
                return ferryCount;
            }

            /**
             * Color methods
             */
            public Color getColor() {
                return color;
            }

            public void setColor(Color color) {
                // null is valid (multicolor), no validation needed for enum
                this.color = color;
            }

            public boolean isMulticolor() {
                return color == null;
            }

            /**
             * Claim state methods
             */
            public String getClaimedBy() {
                return claimedBy;
            }

            /**
             * Claim the edge for a player
             * Throws exception if edge is already claimed
             */
            public void claim(String playerIdentifier) {
                if (getClaimedBy() != null) {
                    throw new IllegalStateException("Route already claimed by " + claimedBy);
                }
                if (playerIdentifier == null || playerIdentifier.trim().isEmpty()) {
                    throw new IllegalArgumentException("Player identifier cannot be null or empty");
                }
                this.claimedBy = playerIdentifier;
            }

            /**
             * Unclaim the edge (make it available again), WILL NEVER BE USED
             */
            public void unclaim() {
                this.claimedBy = null;
            }
        }

        protected WeightedGraph() {
            this.adjacencyList = new HashMap<>();
            this.cities = new HashSet<>();
        }

        /**
         * Add a city to the graph (vertex)
         * Cities must be added before creating edges between them
         */
        protected void addCity(String city) {
            if (city == null || city.trim().isEmpty()) {
                throw new IllegalArgumentException("City name cannot be null or empty");
            }
            city = city.trim().toLowerCase();
            cities.add(city);
            // Initialize empty adjacency list for the city
            adjacencyList.putIfAbsent(city, new ArrayList<>());
        }

        /**
         * Check if a city exists in the graph
         */
        protected boolean hasCity(String city) {
            return cities.contains(city);
        }

        /**
         * Add an edge between two existing cities
         * Validates that both cities exist before creating the connection
         * @param color Color enum value, or null for multicolor/wildcard routes
         */
        public void addEdge(String source, String destination, int weight, boolean isTunnel, int ferryCount, Color color) {
            // Validate that both cities exist
            if (!hasCity(source)) {
                throw new IllegalArgumentException("Source city '" + source + "' does not exist. Add cities first.");
            }
            if (!hasCity(destination)) {
                throw new IllegalArgumentException("Destination city '" + destination + "' does not exist. Add cities first.");
            }
            
            // Validate weight is positive
            if (weight <= 0) {
                throw new IllegalArgumentException("Weight must be positive");
            }

            // Create edge and add to source's adjacency list
            Edge edge = new Edge(destination, weight, isTunnel, ferryCount, color);
            adjacencyList.get(source).add(edge);
        }

        /**
         * Load cities from a text file (one city per line)
         * Uses Scanner with robust looping for file reading
         * Continues processing even if some cities fail validation
         */
        protected int loadCitiesFromFile(String filePath) {
            int successCount = 0;
            int lineNumber = 0;
            
            try (Scanner scanner = new Scanner(new File(filePath))) {
                while (scanner.hasNextLine()) {
                    lineNumber++;
                    String line = scanner.nextLine().trim();
                    
                    // Skip empty lines
                    if (line.isEmpty()) {
                        continue;
                    }
                    
                    try {
                        addCity(line);
                        successCount++;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error on line " + lineNumber + ": " + e.getMessage());
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: City file not found: " + filePath);
                System.out.println("Exception: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error reading city file: " + filePath);
                System.out.println("Exception: " + e.getMessage());
            }
            
            return successCount;
        }

        /**
         * Get an edge between two cities (checks both directions)
         * @return Edge if found, null otherwise
         */
        protected Edge getEdge(String source, String destination) {
            source = source.trim().toLowerCase();
            destination = destination.trim().toLowerCase();
            
            List<Edge> edges = adjacencyList.get(source);
            if (edges != null) {
                for (Edge edge : edges) {
                    if (edge.getDestination().equals(destination)) {
                        return edge;
                    }
                }
            }
            // Check reverse direction (undirected graph)
            edges = adjacencyList.get(destination);
            if (edges != null) {
                for (Edge edge : edges) {
                    if (edge.getDestination().equals(source)) {
                        return edge;
                    }
                }
            }
            return null;
        }

        /**
         * Load edges from a CSV file
         * Format: source,destination,weight,isTunnel,ferryCount,color
         * First line is header and is skipped
         * Uses Scanner with robust looping for file reading
         * Continues processing even if some edges fail validation
         * Color can be empty/null for multicolor routes
         */
        protected int loadEdgesFromFile(String filePath) {
            int successCount = 0;
            int lineNumber = 0;
            boolean isFirstLine = true;
            
            try (Scanner scanner = new Scanner(new File(filePath))) {
                while (scanner.hasNextLine()) {
                    lineNumber++;
                    String line = scanner.nextLine().trim();
                    
                    // Skip header row (first line)
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    // Skip empty lines
                    if (line.isEmpty()) {
                        continue;
                    }
                    
                    // Parse CSV line
                    String[] parts = line.split(",");
                    if (parts.length != 6) {
                        System.out.println("Error on line " + lineNumber + ": Expected 6 columns, found " + parts.length);
                        continue;
                    }
                    
                    try {
                        String source = parts[0].trim();
                        String destination = parts[1].trim();
                        int weight = Integer.parseInt(parts[2].trim());
                        boolean isTunnel = Boolean.parseBoolean(parts[3].trim());
                        int ferryCount = Integer.parseInt(parts[4].trim());
                        String colorStr = parts[5].trim();
                        
                        // Parse color: empty string or "null" or "multicolor" = null (wildcard)
                        // Otherwise, try to match enum value
                        Color color = null;
                        if (!colorStr.isEmpty() && !colorStr.equalsIgnoreCase("null") && !colorStr.equalsIgnoreCase("multicolor")) {
                            try {
                                color = Color.valueOf(colorStr.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error on line " + lineNumber + ": Invalid color '" + colorStr + "'. Valid colors: RED, BLUE, GREEN, YELLOW, BLACK, WHITE, PINK, ORANGE, MULTICOLOR (or empty for multicolor)");
                                continue;
                            }
                        }
                        
                        addEdge(source, destination, weight, isTunnel, ferryCount, color);
                        successCount++;
                    } catch (NumberFormatException e) {
                        System.out.println("Error on line " + lineNumber + ": Invalid number format - " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error on line " + lineNumber + ": " + e.getMessage());
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: Edge file not found: " + filePath);
                System.out.println("Exception: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error reading edge file: " + filePath);
                System.out.println("Exception: " + e.getMessage());
            }
            
            return successCount;
        }
    }

    private WeightedGraph map;
    private ColorDeck colorDeck;
    private DestinationDeck destinationDeck;

    /**
     * Default constructor - creates empty GameMap.
     * Use loadFromFiles() to load map data.
     */
    public GameMap() {
        map = new WeightedGraph();
    }

    /**
     * Constructor that loads map data from files.
     * @param cityFilePath Path to the city file (relative to project root or absolute path)
     * @param edgeFilePath Path to the edge file (relative to project root or absolute path)
     */
    public GameMap(String cityFilePath, String edgeFilePath) {
        map = new WeightedGraph();
        loadFromFiles(cityFilePath, edgeFilePath);
    }

    /**
     * Loads map data from city and edge files.
     * Game class should handle prompting user for file names and call this method.
     * @param cityFilePath Path to the city file
     * @param edgeFilePath Path to the edge file
     * @return true if both files loaded successfully, false otherwise
     */
    public boolean loadFromFiles(String cityFilePath, String edgeFilePath) {
        int citiesLoaded = map.loadCitiesFromFile(cityFilePath);
        int edgesLoaded = map.loadEdgesFromFile(edgeFilePath);
        return citiesLoaded > 0 && edgesLoaded > 0;
    }

    /**
     * Gets the number of cities loaded in the map
     * @return number of cities
     */
    public int getCityCount() {
        return map.cities.size();
    }

    // ============ Deck Accessor Methods ============

    /**
     * Sets the color deck for the game
     * @param colorDeck The color deck to use
     */
    public void setColorDeck(ColorDeck colorDeck) {
        this.colorDeck = colorDeck;
    }

    /**
     * Gets the color deck
     * @return The color deck
     */
    public ColorDeck getColorDeck() {
        return colorDeck;
    }

    /**
     * Sets the destination deck for the game
     * @param destinationDeck The destination deck to use
     */
    public void setDestinationDeck(DestinationDeck destinationDeck) {
        this.destinationDeck = destinationDeck;
    }

    /**
     * Gets the destination deck
     * @return The destination deck
     */
    public DestinationDeck getDestinationDeck() {
        return destinationDeck;
    }
    
    // ============ Public Route Accessor Methods ============

    /**
     * Check if a route exists between two cities
     */
    public boolean routeExists(String city1, String city2) {
        return map.getEdge(city1, city2) != null;
    }

    /**
     * Get who owns a route (null if unclaimed or doesn't exist)
     * Use this for both checking availability and getting owner
     */
    public String getRouteOwner(String city1, String city2) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        return edge != null ? edge.getClaimedBy() : null;
    }

    /**
     * Get the weight (train cost) of a route, -1 if not found
     */
    public int getRouteWeight(String city1, String city2) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        return edge != null ? edge.getWeight() : -1;
    }

    /**
     * Get the color of a route (null if multicolor or doesn't exist)
     */
    public Color getRouteColor(String city1, String city2) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        return edge != null ? edge.getColor() : null;
    }

    /**
     * Check if a route is a tunnel
     */
    public boolean isRouteTunnel(String city1, String city2) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        return edge != null && edge.isTunnel();
    }

    /**
     * Get the ferry count for a route, -1 if not found
     */
    public int getRouteFerryCount(String city1, String city2) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        return edge != null ? edge.getFerryCount() : -1;
    }

    /**
     * Claim a route for a player
     * @return true if successful, false if route doesn't exist or is already claimed
     */
    public boolean claimRoute(String city1, String city2, String playerId) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        if (edge == null || edge.getClaimedBy() != null) {
            return false;
        }
        edge.claim(playerId);
        return true;
    }

    public int getRoutePoints(String city1, String city2) {
        WeightedGraph.Edge edge = map.getEdge(city1, city2);
        if (edge == null) {
            return 0;
        }
        int weight = edge.getWeight();

        switch (weight) {
            case 1: return 1;
            case 2: return 2;
            case 3: return 4;
            case 4: return 7;
            case 5: return 10;
            case 6: return 15;
            case 8: return 23; //Double check Europe board for point total
            default: return 0;
        }
    }

    /**
     * Check if a destination card is completed (path exists between cities using player's routes)
     */
    public boolean destinationCardCompleted(String city1, String city2, String playerId) {
        if (!(map.hasCity(city1.toLowerCase()) && map.hasCity(city2.toLowerCase()))) {
            return false;
        }
        
        // Use a visited set to prevent infinite loops
        Set<String> visited = new HashSet<>();
        return findPath(city1.toLowerCase(), city2.toLowerCase(), playerId, visited);
    }

    /**
     * Recursive helper to find a path between two cities using only player's claimed routes
     */
    private boolean findPath(String current, String destination, String playerId, Set<String> visited) {
        if (current.equals(destination)) {
            return true;
        }
        
        visited.add(current);
        
        // Get all edges from current city
        List<WeightedGraph.Edge> edges = map.adjacencyList.get(current);
        if (edges == null) {
            return false;
        }
        
        for (WeightedGraph.Edge edge : edges) {
            String nextCity = edge.getDestination();
            
            // Only traverse edges claimed by this player and not yet visited
            if (playerId.equals(edge.getClaimedBy()) && !visited.contains(nextCity)) {
                if (findPath(nextCity, destination, playerId, visited)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}