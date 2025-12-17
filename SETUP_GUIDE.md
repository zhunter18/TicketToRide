# Complete Java Setup Guide for Cursor

## Step 1: Install Java JDK

1. **Download Java JDK 17 or later:**
   - Go to: https://adoptium.net/ (recommended - free, open source)
   - Or: https://www.oracle.com/java/technologies/downloads/
   - Download JDK 17 or 21 (LTS versions recommended)
   - Choose Windows x64 installer

2. **Install Java:**
   - Run the installer
   - **IMPORTANT:** Check the box to "Add to PATH" during installation
   - Or manually add Java to PATH:
     - Find where Java installed (usually `C:\Program Files\Java\jdk-XX`)
     - Add `C:\Program Files\Java\jdk-XX\bin` to your system PATH

3. **Verify Installation:**
   - Open a new terminal in Cursor (Terminal → New Terminal)
   - Type: `java -version`
   - You should see something like: `openjdk version "17.0.x"`

## Step 2: Install Java Extensions in Cursor

1. **Open Extensions:**
   - Press `Ctrl+Shift+X` (or click the Extensions icon in the left sidebar)

2. **Install "Extension Pack for Java":**
   - Search for: `Extension Pack for Java`
   - Publisher: Microsoft
   - Click "Install"
   - This installs multiple extensions:
     - Language Support for Java
     - Debugger for Java
     - Test Runner for Java
     - Maven for Java
     - Project Manager for Java
     - Visual Studio IntelliCode

3. **Restart Cursor** after installation

## Step 3: Project Structure

Your project now has this structure:
```
TicketToRide/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── tickettoride/
│                   ├── GameMap.java
│                   └── Main.java
└── README.md
```

## Step 4: How to Code in Cursor (vs IntelliJ)

### Key Differences:
- **No separate "Run" button** - Use terminal commands
- **Auto-complete works** - Just like IntelliJ, press `Ctrl+Space`
- **Error highlighting** - Red squiggles appear automatically
- **Compile & Run** - Use terminal commands (see below)

### Compiling and Running Java:

1. **Open Terminal in Cursor:**
   - Press `` Ctrl+` `` (backtick) or Terminal → New Terminal

2. **Compile:**
   ```bash
   javac src/main/java/com/tickettoride/GameMap.java
   ```

3. **Run:**
   ```bash
   java -cp src/main/java com.tickettoride.Main
   ```

### Using AI Assistant (Cursor):
- **Ask questions** - Just type in chat (like you're doing now!)
- **Generate code** - Ask "create a class for X"
- **Fix errors** - Show me errors and I'll help fix them
- **Refactor** - Ask to rename, move, or restructure code

## Step 5: First Steps

1. After installing Java and extensions, open `Main.java`
2. You should see syntax highlighting (colored code)
3. Try typing code - auto-complete should work
4. Run the program using the terminal commands above

## Troubleshooting

**Java not found:**
- Make sure Java is in your PATH
- Restart Cursor after installing Java
- Try opening a new terminal window

**Extensions not working:**
- Restart Cursor
- Check if Java is installed: `java -version` in terminal
- Go to Settings → search "java.home" and set path if needed

**Need help?**
- Just ask me in the chat! I can help with any step.


