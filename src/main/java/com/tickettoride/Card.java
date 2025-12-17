package com.tickettoride;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Abstract base class for all cards in Ticket to Ride
 * Handles location tracking and common card functionality
 */
public abstract class Card {

    public static final ArrayList<String> locations = new ArrayList<>(Arrays.asList("HAND", "DECK", "DISCARD"));
    
    protected String cardId;
    protected String location;
    protected String playerId;  // null if not in a hand, otherwise player identifier

    /**
     * Constructor for Card
     * @param cardId Unique identifier for the card
     */
    protected Card(String cardId) {
        this.cardId = cardId;
        this.location = "DECK";  // Cards start in deck
        this.playerId = null;
    }

    /**
     * Get the card's unique identifier
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Get the card's current location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Convenience method to set location to DECK or discard from something else, just not a player's hand
     */
    public void setLocation(String location) {
        if (!(location.equals("DECK") || location.equals("DISCARD"))) {
            throw new IllegalArgumentException("Use setLocation(Location.DECK) for DECK location");
        }

        this.location = location;
        this.playerId = null;
    }

    /**
     * Set the card's location to HAND with a player ID
     * @param location Must be HAND
     * @param playerId The player who holds this card
     */
    public void setLocation(String location, String playerId) {
        if (!location.equals("HAND")) {
            throw new IllegalArgumentException("Use setLocation(Location) for DECK or DISCARD locations");

        }
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
        this.location = location;
        this.playerId = playerId;
    }

    /**
     * Get the player ID if card is in a hand, null otherwise
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Check if card is in the discard pile
     */
    public boolean isInPlace(String location) {
        if (!(location.equals("DECK") || location.equals("DISCARD") || location.equals("HAND"))) {
            throw new IllegalArgumentException("Use setLocation(Location.DECK) for DECK location");
        }

        return location.equals(this.location);
    }

    /**
     * Abstract method to get card type
     * @return String representing card type (e.g., "TRAIN", "DESTINATION")
     */
    public abstract String getCardType();

    /**
     * Abstract method for card representation
     */
    @Override
    public abstract String toString();

}