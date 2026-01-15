# Refactoring Analysis: Moving UI to Game Class

This document outlines all the changes needed to move text prompts and user input handling from individual classes to the `Game` class, making the codebase ready for terminal playability and future online implementation.

## Summary

The main principle: **All classes except `Game` should be "dumb" - they perform actions but don't prompt users or print messages. The `Game` class orchestrates everything and handles all user interaction.**

---

## 1. Player.java - Major Changes Needed

### Current Issues:
- Uses `Scanner` for user input in multiple methods
- Has `System.out.println()` statements for error messages and status updates
- Methods prompt users directly instead of accepting parameters

### Methods That Need Refactoring:

#### A. `drawDestinationCards()` (lines 55-92)
**Current:** Prompts user to choose 1 of 3 cards using Scanner
**Change to:**
- Remove Scanner and all System.out.println calls
- Return the array of 3 cards to Game class
- Add a new method: `selectDestinationCard(DestinationCard[] cards, int choice)` that takes the choice (1-3) as parameter
- Game class will prompt user, get choice, then call the selection method

**New signature:**
```java
public DestinationCard[] drawDestinationCards() {
    return destinationDeck.drawDestinations(playerId, 3);
}

public void selectDestinationCard(DestinationCard[] cards, int choice) {
    // Process the choice (1-3), add selected to destinations, discard others
}
```

#### B. `drawColorCard()` (lines 94-138)
**Current:** Prompts user for "M" (mystery) or "V" (visible), then prompts for index if visible
**Change to:**
- Remove Scanner and all System.out.println calls
- Accept parameters: `drawColorCard(boolean isMystery, int visibleIndex)`
- Game class will prompt user, get their choice, then call this method

**New signature:**
```java
public ColorCard drawColorCard(boolean isMystery, int visibleIndex) {
    // isMystery: true = draw from mystery deck, false = draw visible card at index
    // visibleIndex: only used if isMystery is false (0-4, not 1-5)
}
```

#### C. `buildRoute()` (lines 253-313)
**Current:** Has multiple System.out.println statements for errors and status
**Change to:**
- Remove all System.out.println calls
- Return a result object or use exceptions for errors
- Consider creating a `RouteBuildResult` class to return success/failure with error message

**Options:**
1. **Return a result object:**
```java
public class RouteBuildResult {
    private boolean success;
    private String errorMessage;
    private int pointsEarned;
    // getters/setters
}

public RouteBuildResult buildRoute(String city1, String city2, Color colorChoice) {
    // ... validation logic ...
    // Instead of System.out.println, set errorMessage in result
    // Return result with success=true/false
}
```

2. **Or throw exceptions:**
```java
public class RouteException extends Exception {
    public RouteException(String message) { super(message); }
}

public boolean buildRoute(String city1, String city2, Color colorChoice) throws RouteException {
    if (!gameMap.routeExists(city1, city2)) {
        throw new RouteException("Route does not exist");
    }
    // ... etc
}
```

#### D. `destinationCardCompleted()` (lines 315-323)
**Current:** Prints message when destination is completed
**Change to:**
- Remove System.out.println
- Just return boolean (or return points earned)
- Game class will handle the message

**New signature:**
```java
public int checkDestinationCardCompleted(DestinationCard destinationCard) {
    // Returns points earned (0 if not completed, positive if completed)
    // Game class will print the message
}
```

#### E. Missing Setters
**Current:** Player has `colorDeck` and `destinationDeck` fields but no setters
**Add:**
```java
public void setColorDeck(ColorDeck colorDeck) {
    this.colorDeck = colorDeck;
}

public void setDestinationDeck(DestinationDeck destinationDeck) {
    this.destinationDeck = destinationDeck;
}
```

---

## 2. GameMap.java - Constructor Changes

### Current Issue:
- Constructor (lines 315-381) uses Scanner to prompt for city and edge file paths
- Has multiple System.out.println statements

### Change to:
- Remove Scanner from constructor
- Accept file paths as constructor parameters
- Remove all System.out.println calls (or make them optional via a flag)

**New signature:**
```java
public GameMap(String cityFilePath, String edgeFilePath) {
    map = new WeightedGraph();
    int citiesLoaded = map.loadCitiesFromFile(cityFilePath);
    int edgesLoaded = map.loadEdgesFromFile(edgeFilePath);
    // No prompts, just load the files
}
```

**Or keep default constructor but add a static factory method:**
```java
public GameMap() {
    map = new WeightedGraph();
}

public static GameMap createFromFiles(String cityFilePath, String edgeFilePath) {
    GameMap gameMap = new GameMap();
    gameMap.map.loadCitiesFromFile(cityFilePath);
    gameMap.map.loadEdgesFromFile(edgeFilePath);
    return gameMap;
}
```

---

## 3. Game.java - Needs to Handle All UI

### Current State:
- Has some System.out.println statements (these are OK since Game orchestrates)
- Needs to be expanded to handle all user prompts

### What Game Class Needs to Add:

1. **Scanner instance** (or input handler interface for future online support)
2. **Methods to handle all player actions:**
   - `promptDestinationCardChoice(Player player)` - prompts for 1-3 choice
   - `promptColorCardChoice(Player player)` - prompts for M/V and index
   - `promptRouteBuild(Player player)` - prompts for cities and color
   - `promptInitialDestinationSelection(Player player)` - prompts for 2 of 5 cards to keep
   - `displayGameState()` - shows current game status
   - `displayPlayerHand(Player player)` - shows player's cards
   - `displayVisibleCards()` - shows the 5 visible color cards

3. **Error handling:**
   - Catch exceptions from Player methods
   - Display appropriate error messages
   - Allow retry on invalid input

---

## 4. Other Classes - Minor Issues

### Deck.java (lines 29-44)
- Has System.out.println for file loading errors
- **Change:** These are probably OK for file I/O errors (not user prompts), but consider logging instead

### Main.java
- Has Scanner and prompts (lines 13-60)
- **Status:** This is the entry point, so it's acceptable to have prompts here for game setup
- However, consider moving player name collection to Game class for consistency

---

## 5. Recommended Architecture Pattern

### For Terminal Version:
```java
public class Game {
    private Scanner input;
    
    // All UI methods
    private void displayMessage(String message) { ... }
    private int promptInt(String prompt, int min, int max) { ... }
    private String promptString(String prompt) { ... }
    
    // Game flow methods
    public void playTurn(Player player) {
        displayGameState();
        int action = promptAction();
        switch(action) {
            case 1: handleDrawColorCard(player); break;
            case 2: handleDrawDestinationCard(player); break;
            case 3: handleBuildRoute(player); break;
        }
    }
    
    private void handleDrawColorCard(Player player) {
        // Prompt user, get choice
        // Call player.drawColorCard(isMystery, index)
        // Display result
    }
}
```

### For Future Online Version:
Create an interface:
```java
public interface UserInterface {
    void displayMessage(String message);
    int promptInt(String prompt, int min, int max);
    String promptString(String prompt);
    // ... other UI methods
}

public class TerminalUI implements UserInterface {
    private Scanner input;
    // Implementation using Scanner
}

public class OnlineUI implements UserInterface {
    // Implementation using network/websocket
}

public class Game {
    private UserInterface ui;
    
    public Game(UserInterface ui) {
        this.ui = ui;
    }
    // Use ui.displayMessage() instead of System.out.println
}
```

---

## Summary of Changes by File

| File | Changes Needed | Priority |
|------|---------------|----------|
| **Player.java** | Remove Scanner, remove System.out.println, change method signatures to accept parameters | **HIGH** |
| **GameMap.java** | Remove Scanner from constructor, accept file paths as parameters | **HIGH** |
| **Game.java** | Add all UI handling methods, Scanner instance, orchestrate player actions | **HIGH** |
| **Main.java** | Consider moving setup prompts to Game (optional) | **LOW** |
| **Deck.java** | Consider logging instead of System.out.println (optional) | **LOW** |

---

## Testing Checklist

After refactoring, verify:
- [ ] No Scanner instances in Player class
- [ ] No System.out.println in Player class (except maybe debug statements)
- [ ] All player actions can be triggered from Game class
- [ ] Game class handles all user prompts
- [ ] Error messages are displayed by Game class, not Player
- [ ] GameMap can be created without user prompts
- [ ] All existing functionality still works




