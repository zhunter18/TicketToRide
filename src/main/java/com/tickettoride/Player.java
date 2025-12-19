package com.tickettoride;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class Player {


    private String playerId;
    private int points;
    private int trains;
    private Hashtable<Color, List<ColorCard>> hand;
    private List<DestinationCard> destinations;
    private GameMap gameMap;
    private ColorDeck colorDeck;
    private DestinationDeck destinationDeck;


    public Player(String playerId) {
        this.playerId = playerId;
        this.points = 0;
        this.trains = 45; // Each player starts with 45 trains
        this.hand = new Hashtable<Color, List<ColorCard>>();
        this.destinations = new ArrayList<>();
        this.gameMap = null; //TODO: Pass in the game map, will be done differently when implementing the game map
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public int getPoints() {
        return points;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getDestinations() {
        return destinations.toString();
    }

    public int getTrainCount() {
        return trains;
    }



    /**
     * Draws 3 destination cards from the destination deck and allows the player to choose one
     */
    public void drawDestinationCards() {
        Scanner scnr = new Scanner(System.in);
        DestinationCard[] cards = destinationDeck.drawDestinations(playerId,3);
        
        // Will be implemented by clicking the cards in the GUI
        System.out.println("Pick one of the three destination cards:");

        boolean isValid = false;
        while (scnr.hasNext() && !isValid) {
            try {
                isValid = false;
                int choice = Integer.parseInt(scnr.next());
                if (choice >= 1 && choice <= 3) {
                    isValid = true;

                    for (int i = 1; i <= 3; i++) {
                        if (i != choice) {
                            cards[i-1].setLocation("DISCARD");
                            destinationDeck.discardPile.add(cards[i-1]);
                            cards[i-1] = null;
                        }
                        else {
                            destinations.add(cards[i-1]); // i == choice
                            cards[i-1] = null;
                        }
                    }

                }
                else {
                    System.out.println("Invalid input, please enter an integer between 1 and 3");
                }

            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter an integer between 1 and 3");
            }
        }
    }

    public void drawColorCard() {
        Scanner scnr = new Scanner(System.in);
        
        // First validity check: M or V
        String choice = "";
        boolean validChoice = false;
        while (!validChoice) {
            System.out.print("Please type \"M\" for mystery or \"V\" for visible: ");
            choice = scnr.next().toUpperCase();
            validChoice = choice.equals("M") || choice.equals("V");
            if (!validChoice) {
                System.out.println("Invalid input. Please enter M or V.");
            }
        }
        
        ColorCard card;
        if (choice.equals("M")) {
            card = colorDeck.drawMystery(playerId);
        } 
        else {
            // Second validity check: index 1-5
            int index = -1;
            boolean validIndex = false;
            while (!validIndex) {
                System.out.print("Please enter the index of the card (1-5) you want to draw: ");
                try {
                    index = Integer.parseInt(scnr.next());
                    validIndex = index >= 1 && index <= 5;
                    if (!validIndex) {
                        System.out.println("Invalid input. Please enter a number between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 5.");
                }
            }
            card = (ColorCard) colorDeck.drawVisible(index - 1, this.playerId); // Convert to 0-based index
        }
        
        // Add card to hand
        Color cardColor = card.getColor();
        if (hand.get(cardColor) == null) {
            hand.put(cardColor, new ArrayList<>());
        }
        hand.get(cardColor).add(card);
    }

    /**
     * Count how many cards of a specific color the player has
     */
    public int getCardCount(Color color) {
        List<ColorCard> cards = hand.get(color);
        return cards != null ? cards.size() : 0;
    }

    /**
     * Check if player can afford to build a route
     * @param routeColor The color required (null for gray/any color routes)
     * @param cost Number of cards needed
     * @param ferryCount Minimum number of MULTICOLOR cards required
     * @return true if player has enough cards
     */
    public boolean canAffordRoute(Color routeColor, int cost, int ferryCount) {
        int wildcards = getCardCount(Color.MULTICOLOR);
        
        if (routeColor == null) {
            // Gray route - can use any single color + wildcards
            for (Color c : Color.values()) {
                if (c != Color.MULTICOLOR) {
                    int colorCount = getCardCount(c);
                    if (colorCount + wildcards >= cost && wildcards >= ferryCount) {
                        return true;
                    }
                }
            }
            return false;
        } 
        else {
            // Specific color route
            int colorCount = getCardCount(routeColor);
            return (colorCount + wildcards >= cost) && (wildcards >= ferryCount);
        }
    }

    /**
     * Play cards to claim a route. Removes cards from hand and adds to discard.
     * @param colorToUse The color of cards to play (chosen by player for gray routes)
     * @param cost Total cards needed
     * @param ferryCount Minimum wildcards required
     * @return List of cards played (for discarding)
     */
    public List<ColorCard> playCards(Color colorToUse, int cost, int ferryCount) {
        List<ColorCard> played = new ArrayList<>();
        
        List<ColorCard> colorCards = hand.get(colorToUse);
        List<ColorCard> wildcards = hand.get(Color.MULTICOLOR);
        
        if (colorCards == null) colorCards = new ArrayList<>();
        if (wildcards == null) wildcards = new ArrayList<>();

        // First, use required ferries (wildcards)
        for (int i = 0; i < ferryCount && !wildcards.isEmpty(); i++) {
            ColorCard card = wildcards.remove(0);
            card.setLocation("DISCARD");
            played.add(card);
        }

        // Then use colored cards
        int remaining = cost - played.size();
        for (int i = 0; i < remaining && !colorCards.isEmpty(); i++) {
            ColorCard card = colorCards.remove(0);
            card.setLocation("DISCARD");
            played.add(card);
        }

        // Fill rest with wildcards if needed
        remaining = cost - played.size();
        for (int i = 0; i < remaining && !wildcards.isEmpty(); i++) {
            ColorCard card = wildcards.remove(0);
            card.setLocation("DISCARD");
            played.add(card);
        }

        // Update hand
        hand.put(colorToUse, colorCards);
        hand.put(Color.MULTICOLOR, wildcards);

        // Add played cards to discard pile
        for (ColorCard card : played) {
            colorDeck.discardPile.add(card);
        }

        return played;
    }

    /**
     * Handle tunnel extra cost - draw 3 cards and count matches
     * @return number of extra cards needed
     */
    public int handleTunnelDraw(Color routeColor) {
        int extraCost = 0;
        for (int i = 0; i < 3; i++) {
            ColorCard drawn = (ColorCard) colorDeck.drawMystery(playerId);
            drawn.setLocation("DISCARD");
            colorDeck.discardPile.add(drawn);
            
            if (drawn.getColor() == routeColor || drawn.getColor() == Color.MULTICOLOR) {
                extraCost++;
            }
        }
        return extraCost;
    }

    /**
     * Build a route between two cities
     * @param city1 First city
     * @param city2 Second city
     * @param colorChoice The color to use (required for gray routes, must match for colored routes)
     * @return true if route was successfully built
     */
    public boolean buildRoute(String city1, String city2, Color colorChoice) {
        // Validate route exists and is available
        if (!gameMap.routeExists(city1, city2)) {
            System.out.println("Route does not exist");
            return false;
        }
        if (gameMap.getRouteOwner(city1, city2) != null) {
            System.out.println("Route is already claimed");
            return false;
        }

        int cost = gameMap.getRouteWeight(city1, city2);
        int ferryCount = gameMap.getRouteFerryCount(city1, city2);
        boolean isTunnel = gameMap.isRouteTunnel(city1, city2);
        Color routeColor = gameMap.getRouteColor(city1, city2);
        int pointsEarned = gameMap.getRoutePoints(city1, city2);

        // Validate color choice
        if (routeColor != null && colorChoice != routeColor && colorChoice != Color.MULTICOLOR) {
            System.out.println("Must use " + routeColor.toDisplayString() + " cards for this route");
            return false;
        }

        // For gray routes, use the player's chosen color
        Color colorToUse = (routeColor == null) ? colorChoice : routeColor;
        
        // Handle tunnel - may increase cost
        int totalCost = cost;
        if (isTunnel) {
            System.out.println("This is a tunnel! Drawing 3 cards...");
            int extraCost = handleTunnelDraw(colorToUse);
            totalCost = cost + extraCost;
            System.out.println("Extra cost: " + extraCost + " cards. Total: " + totalCost);

            // Check if player can still afford
            if (!canAffordRoute(colorToUse, totalCost, ferryCount)) {
                System.out.println("Cannot afford tunnel extra cost! Route cancelled.");
                return false;
            }
        }

        // Validate train count
        if (totalCost > this.trains) {
            System.out.println("Not enough trains! Need " + cost + ", have " + trains);
            return false;
        }

        // Play the cards
        playCards(colorToUse, totalCost, ferryCount);

        // Claim the route
        gameMap.claimRoute(city1, city2, playerId);
        this.trains -= cost;

        // Award points
        this.points += pointsEarned;
        // checkTrainCount(); //TODO: Implement when game driver is implemented

        System.out.println("Route claimed! Earned " + pointsEarned + " points. Trains remaining: " + trains);
        return true;
    }

    public boolean destinationCardCompleted(DestinationCard destinationCard) {
        boolean completed = gameMap.destinationCardCompleted(destinationCard.getCity1(), destinationCard.getCity2(), playerId);
        if (completed) {
            System.out.println("Destination card completed! Earned " + destinationCard.getPoints() + " points.");
            points += destinationCard.getPoints();
        }

        return completed;
    }


}
