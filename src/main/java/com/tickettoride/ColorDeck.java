package com.tickettoride;

import java.util.Collections;
import com.tickettoride.Deck;
import com.tickettoride.Card;
import com.tickettoride.Color;
import com.tickettoride.ColorCard;

public class ColorDeck extends Deck {

    protected Card[] visibleCards = new Card[5];

    public ColorDeck() {
        super();
        this.loadCardsFromFile("data/colors/europe.csv");
        
        // Initialize visible cards
        refillVisible();
        
        // Check for 3+ of same color
        checkVisible();
    }

    public void loadCardsFromFile(String filePath) {
        super.loadCardsFromFile(filePath);
    }

    
    public void shuffle() {
        super.shuffle();
    }

    public void addCard(Card card) {
        super.addCard(card);
    }

    /**
     * Task: Take file input and parse it into a Card object
     */
    public Card parseCard(String fileLine) {
        String[] parts = fileLine.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid card file line: " + fileLine);
        }
        String cardId = parts[0];
        String color = parts[1];

        try {
            Color cardColor = Color.valueOf(color.toUpperCase());
            return new ColorCard(cardColor, cardId);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid color: " + color);
        }
    }

    public Card drawMystery(String playerId) {
        Card temp = cards.removeFirst();
        temp.setLocation("HAND", playerId);

        if (cards.size() <= 20) { // Boolean to make sure the deck is shuffled when it is less than 20 cards
            super.shuffle();
        }

        return temp;
    }

    public Card drawVisible(int index, String playerId) {
        Card returnCard = visibleCards[index];
        visibleCards[index] = null;
        returnCard.setLocation("HAND", playerId);
        
        // Refill the empty slot and check for 3+ of same color
        refillVisible();
        checkVisible();
        
        return returnCard;
    }

    /**
     * Check if any color appears 3+ times in visible cards.
     * If so, discard only those cards and refill. Repeat until no color has 3+.
     */
    public void checkVisible() {
        boolean needsCheck = true;
        
        while (needsCheck) {
            needsCheck = false;
            
            // Count each color using ordinal as index
            int[] counts = new int[Color.values().length];
            for (Card card : visibleCards) {
                if (card != null) { // All cards will not be null at this point
                    Color color = ((ColorCard) card).getColor();
                    counts[color.ordinal()]++;
                }
            }
            
            // Find if any color has 3+ cards
            Color colorToDiscard = null;
            for (Color color : Color.values()) {
                if (counts[color.ordinal()] >= 3) {
                    colorToDiscard = color;
                }
            }
            
            // If found, discard only those cards
            if (colorToDiscard != null) {
                for (int i = 0; i < 5; i++) {
                    if (visibleCards[i] != null) {
                        Color cardColor = ((ColorCard) visibleCards[i]).getColor();

                        // If the card color is the same as the color to discard, 
                        // it is added to the discard pile and the slot is filled with the next card from the deck
                        if (cardColor == colorToDiscard) {
                            visibleCards[i].setLocation("DISCARD");
                            discardPile.add(visibleCards[i]);
                            visibleCards[i] = null;
                        }
                    }
                }
                
                // Refill empty slots
                refillVisible();
                
                // Need to check again in case new cards also have 3+
                needsCheck = true;
            }
        }
    }

    /**
     * Refill any empty visible card slots from the deck
     */
    private void refillVisible() {
        for (int i = 0; i < 5; i++) {
            if (visibleCards[i] == null && !cards.isEmpty()) {
                visibleCards[i] = cards.removeFirst();
            }
        }

        if (cards.size() <= 20) { // Boolean to make sure the deck is shuffled when it is less than 20 cards
            super.shuffle();
        }

    }
}
