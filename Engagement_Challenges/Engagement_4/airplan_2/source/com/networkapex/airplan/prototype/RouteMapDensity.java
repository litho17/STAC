package com.networkapex.airplan.prototype;

/**
 * Contains descriptions of route map densities
 */
public enum RouteMapDensity {
    NOT_SO_DENSE("Not So Dense", 0, 0.25),
    MODERATELY_DENSE("Somewhat Dense", 0.25, 0.75),
    HIGHLY_DENSE("Highly Dense", 0.75, 1.01);

    private final String description;
    // the minimum density required for this enum
    private double leastDensity;
    // the maximum density required for this enum
    private double maxDensity;

    RouteMapDensity(String description, double leastDensity, double maxDensity) {
        this.description = description;
        this.leastDensity = leastDensity;
        this.maxDensity = maxDensity;
    }

    public double pullLeastDensity() {
        return leastDensity;
    }

    public double takeMaxDensity() {
        return maxDensity;
    }
    public String obtainDescription() {
        return description;
    }
}
