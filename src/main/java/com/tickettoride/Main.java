package com.tickettoride;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
/**
 * Main entry point for the Ticket to Ride game
 */
public class Main {
    public static void main(String[] args) {
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

        GameMap gameMap = new GameMap();

        // Game game = new Game();
        
    }
}

