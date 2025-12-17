package com.tickettoride;

/**
 * Represents a Destination Card in Ticket to Ride
 * Contains two cities and point value for completing the route
 */
public class DestinationCard extends Card {

    private String city1;
    private String city2;
    private int points;
    private boolean completed; //Dependent on gameplay, implementation may vary

    /**
     * Constructor for DestinationCard
     * @param cardId Unique identifier for the card
     * @param city1 First city endpoint
     * @param city2 Second city endpoint
     * @param points Points awarded for completing this destination
     */
    public DestinationCard(String cardId, String city1, String city2, int points) {
        super(cardId);
        this.city1 = city1;
        this.city2 = city2;
        this.points = points;
        this.completed = false;
    }

    // ============ Destination-specific getters ============

    /**
     * Get the first city
     */
    public String getCity1() {
        return city1;
    }

    /**
     * Get the second city
     */
    public String getCity2() {
        return city2;
    }

    /**
     * Get the point value for this destination
     */
    public int getPoints() {
        return points;
    }

    /**
     * Check if destination has been completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Mark the destination as completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // ============ Abstract method implementations from Card ============
    
    /**
     * Returns the card type
     * @return "DESTINATION"
     */
    @Override
    public String getCardType() {
        return "DESTINATION";
    }

    /**
     * String representation of the destination card
     */
    @Override
    public String toString() {
        return String.format("DestinationCard[%s: %s -> %s (%d pts)%s]",
                getCardId(), city1, city2, points, completed ? " COMPLETED" : "");
    }
}
