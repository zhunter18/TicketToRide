package com.tickettoride;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
/**
 * Main entry point for the Ticket to Ride game
 */
public class Main {

    private Game game;
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
    
    public void run() {
        System.out.println("Welcome to Ticket to Ride!");

        Scanner scnr = new Scanner(System.in);

        System.out.print("Please enter the number of players: ");
        int players = 0;
        boolean validInput = false;
        
        while (!validInput) {
            if (scnr.hasNextInt()) {
                players = scnr.nextInt();
                if (players >= 2 && players <= 6) {
                    validInput = true;
                } else {
                    System.out.println("Must input an integer value between 2-6.");
                    System.out.print("Please enter the number of players: ");
                }
            } else {
                // Consume the invalid input
                String invalidInput = scnr.next();
                System.out.println("Invalid input: '" + invalidInput + "'. Must input an integer value between 2-6.");
                System.out.print("Please enter the number of players: ");
            }
        }

        // Prompt for map files
        System.out.print("Please enter the city file name (e.g., american.txt): ");
        String cityFile = scnr.next();
        String cityFilePath = "data/cities/" + cityFile;
        
        System.out.print("Please enter the edge file name (e.g., american.csv): ");
        String edgeFile = scnr.next();
        String edgeFilePath = "data/edges/" + edgeFile;
        
        GameMap gameBoard = new GameMap(cityFilePath, edgeFilePath);
        
        if (gameBoard.getCityCount() == 0) {
            System.out.println("Error: Failed to load map files. Exiting.");
            scnr.close();
            return;
        }
        
        System.out.println("Map loaded successfully!");

        // Create players
        Player[] allPlayers = new Player[players];
        scnr.nextLine(); // Consume leftover newline
        for (int i = 1; i <= players; i++) {
            validInput = false;
            while (!validInput) {
                System.out.print("Please enter the name of player " + i + ": ");
                String playerName = scnr.nextLine().trim();
                
                if (playerName.isEmpty()) {
                    System.out.println("Player name cannot be empty. Please try again.");
                    continue;
                }
                
                // Check for duplicate names
                boolean duplicate = false;
                for (int j = 0; j < i - 1; j++) {
                    if (allPlayers[j] != null && allPlayers[j].getPlayerId().equals(playerName)) {
                        System.out.println("Player name already exists. Please enter a different name.");
                        duplicate = true;
                        break;
                    }
                }
                
                if (!duplicate) {
                    Player player = new Player(playerName);
                    player.setGameMap(gameBoard);
                    allPlayers[i-1] = player;
                    validInput = true;
                }
            }
        }
        
        // Create game
        game = new Game(allPlayers, gameBoard);
        
        System.out.println("Game initialized with " + players + " players and game board.");
        
        // Start the game
        game.startGame();
        
        scnr.close();

    }
}

