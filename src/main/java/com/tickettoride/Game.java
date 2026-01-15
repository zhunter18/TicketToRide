package com.tickettoride;

import java.util.Scanner;
import java.util.Hashtable;
import java.util.List;

public class Game { 
    private Player[] allPlayers;
    private GameMap map;
    private ColorDeck colorDeck;
    private DestinationDeck destinationDeck;
    private int turns;
    private Scanner input;
    private boolean isGameOver;

    public Game(Player[] allPlayers, GameMap map) {
        this.allPlayers = allPlayers;
        this.map = map;
        this.colorDeck = new ColorDeck();
        colorDeck.loadCardsFromFile("data/colors/europe.csv");
        this.destinationDeck = new DestinationDeck();
        destinationDeck.loadCardsFromFile("data/destinations/american.csv");
        this.input = new Scanner(System.in);
        this.isGameOver = false;
        
        // Set decks on GameMap (shared by all players)
        map.setColorDeck(colorDeck);
        map.setDestinationDeck(destinationDeck);
    }

    public void startGame() {
        System.out.println("Game started!");

        System.out.println("Shuffling cards and dealing cards to players...");
        colorDeck.shuffle();
        destinationDeck.shuffle();
        
        // Deal initial color cards to all players
        for (int i = 0; i < 7; i++) {
            for (Player player: allPlayers) {
                colorDeck.drawMystery(player.getPlayerId());
            }
        }

        // Deal initial destination cards and let players choose
        for (Player player: allPlayers) {
            handleInitialDestinationSelection(player);
        }

        System.out.println("Game setup complete! Game commencing...");

        while (!isGameOver) {
            turns = 0;
            for (Player player: allPlayers) {
                displayPlayerHand(player);
                playTurn(player);
                turns++;
            }
        }
    }

    public void playTurn(Player player) {
        displayMessage("What would you like to do?");
        displayMessage("1. Draw a color card    2. Draw a destination card    3. Build a route");
        int choice = promptInt("Enter your choice: ", 1, 3);
        switch (choice) {
            case 1: handleDrawColorCards(player); break;
            case 2: handleDrawDestinationCard(player); break;
            case 3: handleBuildRoute(player); break;
            default: displayMessage("Invalid choice. Please enter a valid choice."); break;
        }
        checkDestinationCards(player);

        displayMessage(player + "'s + turn complete!");
    }

    public void endGame() {
        System.out.println("Game ended!");
        if (input != null) {
            input.close();
        }
    }

    // ============ UI Helper Methods ============

    /**
     * Displays a message to the user
     */
    private void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prompts user for an integer within a range
     * @param prompt The prompt message
     * @param min Minimum valid value (inclusive)
     * @param max Maximum valid value (inclusive)
     * @return The validated integer input
     */
    private int promptInt(String prompt, int min, int max) {
        int value = -1;
        boolean valid = false;
        
        while (!valid) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(input.next());
                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return value;
    }

    /**
     * Prompts user for a string
     * @param prompt The prompt message
     * @return The string input
     */
    private char promptChar(String prompt) {
        System.out.print(prompt);
        char a = input.next().charAt(0);
        return a;
    }

    /**
     * Prompts user for a string (allows spaces)
     * @param prompt The prompt message
     * @return The string input
     */
    private String promptStringLine(String prompt) {
        System.out.print(prompt);
        input.nextLine(); // Consume any leftover newline
        return input.nextLine();
    }

    // ============ Game Action Handlers ============

    /**
     * Handles initial destination card selection (choose 2 of 5 to keep)
     * @param player The player making the selection
     */
    private void handleInitialDestinationSelection(Player player) {
        DestinationCard[] cards = destinationDeck.drawDestinations(player.getPlayerId(), 5);
        
        displayMessage("\n" + player.getPlayerId() + ", you drew 5 destination cards:");
        for (int i = 0; i < 5; i++) {
            displayMessage((i + 1) + ". " + cards[i].getCity1() + " to " + cards[i].getCity2() + " (" + cards[i].getPoints() + " points)");
        }
        
        displayMessage("Pick three cards to keep (enter two numbers 1-5, separated by space):");
        
        int[] choices = new int[3];
        boolean valid = false;
        while (!valid) {
            String inputLine = input.nextLine().trim();
            String[] parts = inputLine.split("\\s+");
            
            if (parts.length == 2) {
                try {
                    int choice1 = Integer.parseInt(parts[0]);
                    int choice2 = Integer.parseInt(parts[1]);
                    int choice3 = Integer.parseInt(parts[2]);
                    
                    if (choice1 >= 1 && choice1 <= 5 && choice2 >= 1 && choice2 <= 5 && choice1 != choice2 && choice3 >= 1 && choice3 <= 5 
                        && choice3 != choice1 && choice3 != choice2) {
                        choices[0] = choice1;
                        choices[1] = choice2;
                        choices[2] = choice3;
                        valid = true;
                    } else {
                        displayMessage("Invalid choices. Please enter three different numbers between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    displayMessage("Invalid input. Please enter three numbers separated by space.");
                }
            } else {
                displayMessage("Please enter exactly three numbers separated by space.");
            }
        }
        
        // Keep the two selected cards, discard the other three
        for (int i = 0; i < 5; i++) {
            boolean keep = (i + 1 == choices[0] || i + 1 == choices[1] || i + 1 == choices[2]);
            if (keep) {
                player.getDestinationCards().add(cards[i]);
            } else {
                cards[i].setLocation("DISCARD");
                map.getDestinationDeck().discardPile.add(cards[i]);
            }
        }
        
        displayMessage("Destination cards selected!");
    }

    /**
     * Handles drawing a destination card during the game
     * @param player The player drawing the card
     */
    public void handleDrawDestinationCard(Player player) {
        DestinationCard[] cards = player.drawDestinationCards();
        
        displayMessage("\n" + player.getPlayerId() + ", you drew 3 destination cards:");
        for (int i = 0; i < 3; i++) {
            displayMessage((i + 1) + ". " + cards[i].getCity1() + " to " + cards[i].getCity2() + " (" + cards[i].getPoints() + " points)");
        }
        
        int choice = promptInt("Pick one card to keep (1-3): ", 1, 3);
        
        if (player.selectDestinationCard(cards, choice)) {
            displayMessage("Destination card selected!");
            
        } else {
            displayMessage("Error selecting destination card.");
        }
    }

    /**
     * Handles drawing two color cards
     * @param player The player drawing the card
     * Returns true if the player drew two color cards, false if they did not
     */
    public void handleDrawColorCards(Player player) {
        for (int drawn2 = 2; drawn2 > 0; drawn2--) {
            displayMessage("\n" + player.getPlayerId() + ", choose a color card:");
            displayMessage("M - Draw from mystery deck");
            displayMessage("V - Draw from visible cards");
            
            char choice = ' ';
            boolean isMystery = false;
            boolean isVisible = false;


            while (!isMystery && !isVisible) {
                choice = promptChar("Enter M or V: ");
                isMystery = choice == 'M';
                isVisible = choice == 'V';
                if (!isMystery && !isVisible) {
                    displayMessage("Invalid input. Please enter M or V.");
                }
            }
            
            int visibleIndex = -1;
            
            if (!isMystery) {
                // Show visible cards
                displayMessage("\nVisible cards:");
                Card[] visibleCards = colorDeck.getVisibleCards();

                for (int j = 0; j < 5; j++) {
                    if (visibleCards[j] != null) {
                        ColorCard card = (ColorCard) visibleCards[j];
                        displayMessage((j + 1) + ". " + card.getColor().toDisplayString());

                    } else {
                        displayMessage((i + 1) + ". (empty)");
                    }
                }
                
                visibleIndex = promptInt("Enter the index of the card you want (1-5): ", 1, 5) - 1;
            }
            
            // Player method does the drawing and adds the card to the player's hand based on index and visibility
            ColorCard drawn = player.drawColorCard(isMystery, visibleIndex);
            if (drawn != null) {
                displayMessage("Drew " + drawn.getColor().toDisplayString() + " card!");
            } else {
                displayMessage("Error drawing card. Please try again.");
                drawn2++;
            }
        }
    }

    /**
     * Handles building a route
     * @param player The player building the route
     */
    public void handleBuildRoute(Player player) {
        displayMessage("\n" + player.getPlayerId() + ", build a route:");
        
        String city1 = promptStringLine("Enter first city: ").trim();
        String city2 = promptStringLine("Enter second city: ").trim();
        
        // Check if route exists and get its color
        if (!map.routeExists(city1, city2)) {
            displayMessage("Route does not exist between " + city1 + " and " + city2 + ".");
            return;
        }
        
        Color routeColor = map.getRouteColor(city1, city2);
        Color colorChoice = null;
        
        // If it's a gray route, player must choose a color
        if (routeColor == null) {
            displayMessage("This is a gray route. Choose a color to use:");
            displayMessage("Available colors: RED, BLUE, GREEN, YELLOW, BLACK, WHITE, PINK, ORANGE");
            
            boolean validColor = false;
            while (!validColor) {
                String colorStr = promptStringLine("Enter color: ").toUpperCase();
                try {
                    colorChoice = Color.valueOf(colorStr);
                    if (colorChoice == Color.MULTICOLOR) {
                        displayMessage("Cannot use MULTICOLOR as the base color. Choose a specific color.");
                    } else {
                        validColor = true;
                    }
                } catch (IllegalArgumentException e) {
                    displayMessage("Invalid color. Please enter a valid color name.");
                }
            }
        } else {
            colorChoice = routeColor;
        }
        
        // Check if this is a tunnel and handle tunnel drawing live
        int extraTunnelCost = 0;
        boolean isTunnel = map.isRouteTunnel(city1, city2);
        if (isTunnel) {
            displayMessage("\nThis is a tunnel! Drawing 3 cards to determine extra cost...");
            extraTunnelCost = handleTunnelDraw(colorChoice);
            displayMessage("Extra tunnel cost: " + extraTunnelCost + " card(s).");
        }
        
        RouteBuildResult result = player.buildRoute(city1, city2, colorChoice, extraTunnelCost);
        
        if (result.isSuccess()) {
            displayMessage("Route claimed! Earned " + result.getPointsEarned() + " points.");
            displayMessage("Trains remaining: " + result.getTrainsRemaining());
            
            // Check for completed destination cards (only checks incomplete ones, so it's efficient)
            checkDestinationCards(player);
        } else {
            displayMessage("Failed to build route: " + result.getErrorMessage());
        }
    }

    /**
     * Handles tunnel drawing with live card reveals for suspense
     * @param routeColor The color needed for the route
     * @return Number of extra cards needed (0-3)
     */
    private int handleTunnelDraw(Color routeColor) {
        int extraCost = 0;
        
        for (int i = 1; i <= 3; i++) {
            displayMessage("Drawing card " + i + " of 3...");
            ColorCard drawn = colorDeck.drawMysteryToDiscard();
            
            // Display the drawn card
            String cardColor = drawn.getColor().toDisplayString();
            displayMessage("  → Drew: " + cardColor);
            
            // Check if it matches (route color or wildcard)
            if (drawn.getColor() == routeColor || drawn.getColor() == Color.MULTICOLOR) {
                extraCost++;
                displayMessage("  → Match! Extra cost increased to " + extraCost);
            } else {
                displayMessage("  → No match.");
            }
        }
        
        return extraCost;
    }

    /**
     * Checks all destination cards for a player and awards points if completed
     * @param player The player to check
     */
    private void checkDestinationCards(Player player) {
        for (DestinationCard dest : player.getDestinationCards()) {
            int pointsEarned = player.checkDestinationCardCompleted(dest);
            if (pointsEarned > 0) {
                displayMessage("Destination card completed! " + dest.getCity1() + " to " + dest.getCity2() + " - Earned " + pointsEarned + " points!");
            }
        }
    }

    /**
     * Displays the current game state
     */
    public void displayGameState() {
        displayMessage("\n=== Game State ===");
        for (Player player : allPlayers) {
            displayMessage(player.getPlayerId() + ": " + player.getPoints() + " points, " + player.getTrainCount() + " trains remaining");
        }
        displayMessage("==================\n");
    }

    /**
     * Displays a player's hand
     * @param player The player whose hand to display
     */
    public void displayPlayerHand(Player player) {
        displayMessage("\n" + player.getPlayerId() + "'s hand:");
        Hashtable<Color, List<ColorCard>> hand = player.getHand();
        for (Color color : Color.values()) {
            List<ColorCard> cards = hand.get(color);
            if (cards != null && !cards.isEmpty()) {
                displayMessage(color.toDisplayString() + ": " + cards.size());
            }
        }
        displayMessage("Destination cards: " + player.getDestinations());
    }

    // ============ Getters ============

    public ColorDeck getColorDeck() {
        return colorDeck;
    }

    public DestinationDeck getDestinationDeck() {
        return destinationDeck;
    }

    public GameMap getMap() {
        return map;
    }

    public Player[] getAllPlayers() {
        return allPlayers;
    }
}