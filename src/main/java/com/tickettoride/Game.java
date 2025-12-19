package com.tickettoride;



public class Game { 
    private Player[] allPlayers;
    private GameMap map;
    private ColorDeck colorDeck;
    private DestinationDeck destinationDeck;
    private int turnCount;

    public Game(Player[] allPlayers, GameMap map) {
        this.allPlayers = allPlayers;
        this.map = map;
        this.colorDeck = new ColorDeck();
        colorDeck.loadCardsFromFile("data/colors/europe.csv");
        this.destinationDeck = new DestinationDeck();
        destinationDeck.loadCardsFromFile("data/destinations/american.csv");
    }

    public void startGame() {
        System.out.println("Game started!");

        System.out.println("Shuffling cards and dealing cards to players...");
        colorDeck.shuffle();
        destinationDeck.shuffle();
        
        for (int i = 0; i < 7; i++) {
            for (Player player: allPlayers) {
                colorDeck.drawMystery(player.getPlayerId());
            }
        }

        
        for (Player player: allPlayers) {
            destinationDeck.drawDestinations(player.getPlayerId(),5);
        }

        System.out.println("Pick two of the five destination cards to discard:");
    

    }

    public void endGame() {
        System.out.println("Game ended!");
    }

}