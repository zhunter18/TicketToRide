package com.tickettoride;

public enum Color {
    RED, BLUE, GREEN, YELLOW, BLACK, WHITE, PINK, ORANGE, MULTICOLOR;

    @Override
    public String toString() {
        // Returns lowercase name (e.g., "red", "blue", "multicolor")
        return name().toLowerCase();
    }

    /**
     * Returns a display-friendly name (e.g., "Red", "Blue", "Wild")
     */
    public String toDisplayString() {
        if (this == MULTICOLOR) {
            return "Wild";
        }
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}

