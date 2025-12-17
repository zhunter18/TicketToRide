package com.tickettoride;

import java.util.List;

public class Player {


    private String playerId;
    private int points;
    private int trains;
    private List<ColorCard> hand;
    private List<DestinationCard> destinations;


    public Player(String playerId) {
        this.playerId = playerId;
    }


}
