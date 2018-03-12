package com.roboticcusp.organizer.framework;

/**
 * Contains a flight's possible weight types.
 * <p/>
 * This enum is meant to be used to describe which weight type should be used when
 * running algorithms on a route map.
 */
public enum FlightWeightType {
    COST("Cost", "Cost"),
    DISTANCE("Distance", "Distance"),
    TIME("Time", "Time"),
    CREW_MEMBERS("Crew", "Number of Crew Members"),
    WEIGHT("Weight", "Weight Capacity"),
    PASSENGERS("Passengers", "Number of Passengers");

    private final String description;
    private final String identifier;

    FlightWeightType(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;

    }

    /**
     * Returns a Weight Type given its identifier
     */
    public static FlightWeightType fromString(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            FlightWeightType[] values = FlightWeightType.values();
            for (int q = 0; q < values.length; q++) {
                FlightWeightType weightType = values[q];
                if (identifier.equalsIgnoreCase(weightType.identifier)) {
                    return weightType;
                }
            }
        }
        return null;
    }

    public String grabDescription() {
        return description;
    }

    @Override
    public String toString() {
        return identifier;
    }

}
