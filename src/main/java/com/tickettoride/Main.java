package com.tickettoride;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
/**
 * Main entry point for the Ticket to Ride game
 */
public class Main {

    private Game game;
    public void main(String[] args) {
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

        GameMap gameBoard = new GameMap();
        game = new Game(players, gameBoard);

        System.out.println("Game initialized with " + players + " players and game board.");
        

        Player[] allPlayers = new Player[players];
        for (int i = 1; i <= players; i++) {
            validInput = false;
            System.out.println("Please enter the name of player" + i + ": ");
            while (!validInput) {
            String playerName = scnr.nextLine();
            for (Player a: allPlayers) {
                if (a.getPlayerId().equals(playerName)) {
                    System.out.println("Player name already exists. Please enter a different name.");
                    validInput = false;
                }
            }
            Player player = new Player(playerName);
            player.setGameMap(gameBoard);
            allPlayers[i-1] = player;
        }

    }
}

