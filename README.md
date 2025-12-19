# TicketToRide
Ticket to ride online version

## Current Implementation Status

### Backend Game Logic (Java)
- **Game Map System**: Weighted graph representing routes between cities
  - Supports colored and multicolor (gray) routes
  - Tracks route ownership and availability
  - Handles tunnels and ferries
  - Pathfinding for destination card completion validation
  - Loads map data from CSV files (cities and edges)

- **Player Management**: Player class with:
  - Hand management (color cards organized by color)
  - Train piece tracking (45 trains per player)
  - Points system
  - Destination card tracking
  - Route building with validation (color requirements, cost, ferry rules)

- **Card System**:
  - **ColorDeck**: Train cards with visible face-up cards (5 slots), auto-discard when 3+ of same color appear
  - **DestinationDeck**: Destination cards connecting two cities
  - Card location tracking (DECK, HAND, DISCARD)
  - Shuffle and deck management

- **Game Mechanics**:
  - Route building with color/cost validation
  - Tunnel mechanics (draw 3 cards, count matches for extra cost)
  - Ferry requirements (minimum wildcard cards)
  - Destination card completion checking via pathfinding
  - Turn-based structure (initialized, not fully implemented)

- **Data Management**: File-based loading for:
  - Cities (text files)
  - Routes/edges (CSV with weight, tunnel, ferry, color)
  - Color cards (CSV)
  - Destination cards (CSV)

### Frontend Status
- **No frontend currently implemented** — the game is console-based
- Uses `Scanner` for command-line input
- Code comments indicate planned GUI (e.g., "Will be implemented by clicking the cards in the GUI")
- No HTML, CSS, or JavaScript files present

### Technology Stack
- **Language**: Java (pure Java, no frameworks)
- **Architecture**: Object-oriented design with inheritance and polymorphism
- **Data Structures**: HashMaps, ArrayLists, Deques, Hashtables
- **File I/O**: Standard Java `Scanner` and `File` classes
- **Build System**: None detected (no Maven/Gradle configuration)

### What's Almost Done
- Core game logic is functional
- Missing: complete game loop, turn management, win conditions
- Missing: web frontend (HTML/CSS/JavaScript) or desktop GUI
- Missing: multiplayer networking (currently single-player console)

### For Your Resume
You could describe this as:

> **Ticket to Ride Game Engine (Java)** — Developed a console-based implementation of the Ticket to Ride board game with a weighted graph map system, card deck management, and route-building mechanics. Implemented player management, destination card validation using pathfinding algorithms, and file-based data loading for game maps and cards. Built with object-oriented design principles, supporting game rules including tunnels, ferries, and multicolor routes. *[Note: Frontend/GUI implementation planned for future development]*

The project demonstrates solid Java skills, data structure usage, graph algorithms, and game logic implementation, even without a frontend.