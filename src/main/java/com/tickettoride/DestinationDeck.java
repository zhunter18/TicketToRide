package com.tickettoride;

import com.tickettoride.Deck;
import com.tickettoride.Card;

public class DestinationDeck extends Deck {

    public DestinationDeck() {
        super();
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

    public Card parseCard(String fileLine) {
        String[] parts = fileLine.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid card file line: " + fileLine);
        }
        String cardId = parts[0];
        String city1 = parts[1];
        String city2 = parts[2];
        try { // try to parse the points as an integer
            int points = Integer.parseInt(parts[3]);
            if (points < 0) {
                throw new IllegalArgumentException("Invalid points: " + points);
            }
            return new DestinationCard(cardId, city1, city2, points);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Point value must be a positive integer: " + parts[3]);
        }
    }

    public DestinationCard[] drawDestinations(String playerId,int numCards) {
        DestinationCard[] choices = new DestinationCard[numCards];
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        for (int i = 0; i < numCards; i++) {
            choices[i] = (DestinationCard) cards.removeFirst();
            choices[i].setLocation("HAND", playerId);
        }
        // I NEED TO MAKE A METHOD TO CHOOSE THE BEST 3 CARDS FROM THE 3 CHOICES IN THE PLAYER CLASS
        return choices;
    }
}
