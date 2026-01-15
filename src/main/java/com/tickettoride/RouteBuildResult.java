package com.tickettoride;

/**
 * Result object returned from Player.buildRoute() to communicate success/failure
 * and provide information for UI display
 */
public class RouteBuildResult {
    private boolean success;
    private String errorMessage;
    private int pointsEarned;
    private int trainsRemaining;
    private int extraTunnelCost; // 0 if not a tunnel, or the extra cost if it was

    public RouteBuildResult(boolean success, String errorMessage, int pointsEarned, int trainsRemaining, int extraTunnelCost) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.pointsEarned = pointsEarned;
        this.trainsRemaining = trainsRemaining;
        this.extraTunnelCost = extraTunnelCost;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public int getTrainsRemaining() {
        return trainsRemaining;
    }

    public int getExtraTunnelCost() {
        return extraTunnelCost;
    }
}

