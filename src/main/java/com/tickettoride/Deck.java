package com.tickettoride;

import java.util.Deque;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Scanner;
import com.tickettoride.Card;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

public abstract class Deck {

    protected Deque<Card> cards;
    protected List<Card> discardPile;

    public Deck() {
        cards = new ArrayDeque<>();
        discardPile = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }  

    public void loadCardsFromFile(String filePath) {
        System.out.print("Please enter the deck file name:  " + filePath);
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String fileLine = scanner.nextLine();
                Card card = this.parseCard(fileLine);
                
                cards.add(card);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Error: Card file not found: " + filePath);
            System.out.println("Exception: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("Error reading card file: " + filePath);
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void shuffle() {
        Collections.shuffle(discardPile);

        for (int i = 0; i < discardPile.size(); i++) { //Needs to be Card because of DiscardPile being implemented from generic class
            Card card = discardPile.get(i);
            card.setLocation("DECK");
            this.addCard(card);
        }
        discardPile.clear();
    }

    public abstract Card parseCard(String fileLine);
}
