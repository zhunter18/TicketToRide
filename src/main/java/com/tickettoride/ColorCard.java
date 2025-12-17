package com.tickettoride;

import com.tickettoride.Color; // Not necessary, but good practice

/**
 * TrainCard class - minimal implementation
 * Extends Card and implements required abstract methods
 */
public class ColorCard extends Card {
    private Color color;

    /**
     * Constructor
     * @param color The color of the train card
     * @param cardId Unique identifier for the card
     */
    public ColorCard(Color color, String cardId) {
        super(cardId);
        this.color = color;
    }

    /**
     * Get the color of the train card
     */
    public Color getColor() {
        return color;
    }

    /**
     * Required implementation of abstract method from Card
     */
    @Override
    public String getCardType() {
        return "TRAIN";
    }

    /**
     * Required implementation of abstract method from Card
     */
    @Override
    public String toString() {
        return "This card is a " + color + " train with ID number " + getCardId();
    }
}