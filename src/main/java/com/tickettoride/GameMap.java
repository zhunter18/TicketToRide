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
import com.tickettoride.Color; // Not necessary, but good practice

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

            public boolean isClaimed() {
                return claimedBy != null;
            }

            /**
             * Claim the edge for a player
             * Throws exception if edge is already claimed
             */
            public void claim(String playerIdentifier) {
                if (isClaimed()) {
                    throw new IllegalStateException("Route already claimed by " + claimedBy);
                }
                if (playerIdentifier == null || playerIdentifier.trim().isEmpty()) {
                    throw new IllegalArgumentException("Player identifier cannot be null or empty");
                }
                this.claimedBy = playerIdentifier;
            }

            /**
             * Unclaim the edge (make it available again)
             */
            public void unclaim() {
                this.claimedBy = null;
            }

            /**
             * Set claimedBy directly (use claim() method instead for validation)
             * This method is kept for backward compatibility but should use claim() instead
             */
            public void setClaimedBy(String claimedBy) {
                this.claimedBy = claimedBy;
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
                                System.out.println("Error on line " + lineNumber + ": Invalid color '" + colorStr + "'. Valid colors: RED, BLUE, GREEN, YELLOW, BLACK, WHITE, PINK, ORANGE, PURPLE, MULTICOLOR (or empty for multicolor)");
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

    public GameMap() {
        map = new WeightedGraph();
        Scanner scanner = new Scanner(System.in);

        // Retry loop for city file
        String cityFilePath = null;
        boolean cityFileValid = false;
        while (!cityFileValid) {
            System.out.print("Please enter the name of the city file (or 'quit' to exit): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting GameMap initialization...");
                scanner.close();
                return;
            }
            
            if (input.isEmpty()) {
                System.out.println("File name cannot be empty. Please try again.");
                continue;
            }
            
            cityFilePath = "data/cities/" + input;
            int citiesLoaded = map.loadCitiesFromFile(cityFilePath);
            
            if (citiesLoaded > 0) {
                cityFileValid = true;
                System.out.println("Successfully loaded " + citiesLoaded + " cities from " + cityFilePath);
            } else {
                System.out.println("No cities were loaded. Please check the file path and try again.");
                System.out.println("(Make sure the file exists and contains valid city names)");
            }
        }

        // Retry loop for edge file
        String edgeFilePath = null;
        boolean edgeFileValid = false;
        while (!edgeFileValid) {
            System.out.print("Please enter the name of the edge file (or 'quit' to exit): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting GameMap initialization...");
                scanner.close();
                return;
            }
            
            if (input.isEmpty()) {
                System.out.println("File name cannot be empty. Please try again.");
                continue;
            }
            
            edgeFilePath = "data/edges/" + input;
            int edgesLoaded = map.loadEdgesFromFile(edgeFilePath);
            
            if (edgesLoaded > 0) {
                edgeFileValid = true;
                System.out.println("Successfully loaded " + edgesLoaded + " edges from " + edgeFilePath);
            } else {
                System.out.println("No edges were loaded. Please check the file path and try again.");
                System.out.println("(Make sure the file exists and contains valid edge data)");
            }
        }

        scanner.close();
        System.out.println("GameMap initialized successfully!");
    }
    
    
}