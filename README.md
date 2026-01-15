# TicketToRide
Ticket to Ride - Terminal-based Java Implementation

## Current Implementation Status

### Backend Game Logic (Java)
- **Game Map System**: Weighted graph representing routes between cities
  - Supports colored and multicolor (gray) routes
  - Tracks route ownership and availability
  - Handles tunnels and ferries
  - Pathfinding for destination card completion validation
  - Loads map data from CSV files (cities and edges)
  - Decks (ColorDeck and DestinationDeck) stored in GameMap for shared access

- **Player Management**: Player class with:
  - Hand management (color cards organized by color)
  - Train piece tracking (45 trains per player)
  - Points system
  - Destination card tracking with completion status
  - Route building with validation (color requirements, cost, ferry rules)
  - Pure logic layer - no UI dependencies

- **Card System**:
  - **ColorDeck**: Train cards with visible face-up cards (5 slots), auto-discard when 3+ of same color appear
  - **DestinationDeck**: Destination cards connecting two cities
  - Card location tracking (DECK, HAND, DISCARD)
  - Shuffle and deck management
  - Direct discard drawing for tunnel mechanics

- **Game Mechanics**:
  - ✅ **Complete game loop** with turn-based play
  - ✅ **Turn management** - players take turns in sequence
  - Route building with color/cost validation
  - **Live tunnel mechanics** - draws 3 cards one at a time with suspense, reveals each card as drawn
  - Ferry requirements (minimum wildcard cards)
  - Destination card completion checking via pathfinding
  - Destination card completion tracking (prevents duplicate point awards)
  - Win condition checking (game ends when player has ≤2 trains)

- **UI Architecture**:
  - All user interaction centralized in Game class
  - Player and GameMap classes are pure logic (no UI)
  - Terminal-based interface with Scanner input
  - Structured for future online/multiplayer implementation

- **Data Management**: File-based loading for:
  - Cities (text files)
  - Routes/edges (CSV with weight, tunnel, ferry, color)
  - Color cards (CSV)
  - Destination cards (CSV)

### Frontend Status
- **Terminal-based console interface** — fully functional
- Uses `Scanner` for command-line input
- All UI methods in Game class (displayMessage, promptInt, promptChar, etc.)
- Ready for frame-based terminal UI enhancement (ANSI escape codes)

### Technology Stack
- **Language**: Java (pure Java, no frameworks)
- **Architecture**: Object-oriented design with separation of concerns
  - Game class = UI/Orchestration layer
  - Player class = Logic layer (no UI)
  - GameMap class = Game state container
- **Data Structures**: HashMaps, ArrayLists, Deques, Hashtables
- **File I/O**: Standard Java `Scanner` and `File` classes
- **Build System**: None detected (no Maven/Gradle configuration)

### Completed Features
- ✅ Core game logic (route building, card drawing, destination checking)
- ✅ Complete game loop with turn management
- ✅ Player actions (draw color cards, draw destination cards, build routes)
- ✅ Tunnel mechanics with live card reveals
- ✅ Destination card completion tracking
- ✅ Win condition detection
- ✅ UI refactoring (all prompts in Game class)
- ✅ Deck management (decks stored in GameMap)

### In Progress / Planned
- Final scoring (subtract incomplete destination card points)
- Winner announcement
- Frame-based terminal UI (updating display instead of scrolling)
- Web frontend (HTML/CSS/JavaScript) or desktop GUI
- Multiplayer networking