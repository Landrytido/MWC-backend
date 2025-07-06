package com.mywebcompanion.enums;

public enum EventMode {
    PRESENTIEL("Présentiel"),
    DISTANCIEL("Distanciel");

    private final String displayName;

    EventMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
