package com.tickettoride;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

public class Player {


    private String playerId;
    private int points;
    private int trains;
    private Hashtable<Color, List<ColorCard>> hand;
    private List<DestinationCard> destinations;
    private GameMap gameMap;


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
     * Gets the player's hand organized by color
     * @return Hashtable mapping Color to List of ColorCards
     */
    public Hashtable<Color, List<ColorCard>> getHand() {
        return hand;
    }

    /**
     * Gets the list of destination cards the player has
     * @return List of DestinationCards
     */
    public List<DestinationCard> getDestinationCards() {
        return destinations;
    }

    /**
     * Draws 3 destination cards from the destination deck.
     * Game class should prompt user for choice and call selectDestinationCard()
     * @return Array of 3 destination cards for the player to choose from
     */
    public DestinationCard[] drawDestinationCards() {
        return gameMap.getDestinationDeck().drawDestinations(playerId, 3);
    }

    /**
     * Selects one destination card from the 3 drawn cards.
     * The selected card is added to player's destinations, others are discarded.
     * @param cards Array of 3 destination cards (from drawDestinationCards())
     * @param choice Player's choice (1-3, where 1 is first card, 2 is second, 3 is third)
     * @return true if selection was successful, false if invalid choice
     */
    public boolean selectDestinationCard(DestinationCard[] cards, int choice) {
        if (choice < 1 || choice > 3) {
            return false;
        }
        if (cards == null || cards.length != 3) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (i == choice - 1) {
                // Selected card - add to destinations
                destinations.add(cards[i]);
            } else {
                // Other cards - discard
                if (cards[i] != null) {
                    cards[i].setLocation("DISCARD");
                    gameMap.getDestinationDeck().discardPile.add(cards[i]);
                }
            }
        }
        return true;
    }

    /**
     * Draws a color card based on player's choice.
     * Game class should prompt user and call this method with their choice.
     * @param isMystery true to draw from mystery deck, false to draw visible card
     * @param visibleIndex index of visible card to draw (0-4), only used if isMystery is false
     * @return The drawn ColorCard, or null if invalid parameters
     */
    public ColorCard drawColorCard(boolean isMystery, int visibleIndex) {
        ColorCard card;
        
        if (isMystery) {
            card = gameMap.getColorDeck().drawMystery(playerId);
        } else {
            // Validate visible index
            if (visibleIndex < 0 || visibleIndex >= 5) {
                return null;
            }
            card = (ColorCard) gameMap.getColorDeck().drawVisible(visibleIndex, this.playerId);
        }
        
        if (card == null) {
            return null;
        }
        
        // Add card to hand
        Color cardColor = card.getColor();
        if (hand.get(cardColor) == null) {
            hand.put(cardColor, new ArrayList<>());
        }
        hand.get(cardColor).add(card);
        
        return card;
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
            gameMap.getColorDeck().discardPile.add(card);
        }

        return played;
    }

    
    /**
     * Build a route between two cities
     * @param city1 First city
     * @param city2 Second city
     * @param colorChoice The color to use (required for gray routes, must match for colored routes)
     * @param extraTunnelCost Extra cost from tunnel drawing (0 if not a tunnel)
     * @return RouteBuildResult containing success status, error message (if failed), points earned, and trains remaining
     */
    public RouteBuildResult buildRoute(String city1, String city2, Color colorChoice, int extraTunnelCost) {
        // Validate route exists and is available
        if (!gameMap.routeExists(city1, city2)) {
            return new RouteBuildResult(false, "Route does not exist", 0, trains, 0);
        }
        if (gameMap.getRouteOwner(city1, city2) != null) {
            return new RouteBuildResult(false, "Route is already claimed", 0, trains, 0);
        }

        int cost = gameMap.getRouteWeight(city1, city2);
        int ferryCount = gameMap.getRouteFerryCount(city1, city2);
        Color routeColor = gameMap.getRouteColor(city1, city2);
        int pointsEarned = gameMap.getRoutePoints(city1, city2);

        // Validate color choice
        if (routeColor != null && colorChoice != routeColor && colorChoice != Color.MULTICOLOR) {
            return new RouteBuildResult(false, "Must use " + routeColor.toDisplayString() + " cards for this route", 0, trains, 0);
        }

        // For gray routes, use the player's chosen color
        Color colorToUse = (routeColor == null) ? colorChoice : routeColor;
        
        // Add tunnel extra cost (calculated in Game class via live card drawing)
        int totalCost = cost + extraTunnelCost;
        
        // Check if player can still afford after tunnel cost
        if (extraTunnelCost > 0 && !canAffordRoute(colorToUse, totalCost, ferryCount)) {
            return new RouteBuildResult(false, "Cannot afford tunnel extra cost! Need " + totalCost + " cards but only have " + (getCardCount(colorToUse) + getCardCount(Color.MULTICOLOR)), 0, trains, extraTunnelCost);
        }

        // Validate train count
        if (totalCost > this.trains) {
            return new RouteBuildResult(false, "Not enough trains! Need " + totalCost + ", have " + trains, 0, trains, extraTunnelCost);
        }

        // Validate player has enough cards
        if (!canAffordRoute(colorToUse, totalCost, ferryCount)) {
            return new RouteBuildResult(false, "Not enough cards! Need " + totalCost + " " + colorToUse.toDisplayString() + " cards (with at least " + ferryCount + " wildcards)", 0, trains, extraTunnelCost);
        }

        // Play the cards
        playCards(colorToUse, totalCost, ferryCount);

        // Claim the route
        gameMap.claimRoute(city1, city2, playerId);
        this.trains -= totalCost;

        // Award points
        this.points += pointsEarned;
        // checkTrainCount(); //TODO: Implement when game driver is implemented

        return new RouteBuildResult(true, null, pointsEarned, trains, extraTunnelCost);
    }

    /**
     * Checks if a destination card is completed and awards points if so.
     * Game class should display the message about completion.
     * @param destinationCard The destination card to check
     * @return Points earned (0 if not completed or already awarded, positive if newly completed)
     */
    public int checkDestinationCardCompleted(DestinationCard destinationCard) {
        // Skip if already completed and points awarded
        if (destinationCard.isCompleted()) {
            return 0;
        }
        
        boolean completed = gameMap.destinationCardCompleted(destinationCard.getCity1(), destinationCard.getCity2(), playerId);
        if (completed) {
            int pointsEarned = destinationCard.getPoints();
            points += pointsEarned;
            destinationCard.setCompleted(true); // Mark as completed to prevent duplicate awards
            return pointsEarned;
        }
        return 0;
    }
}
