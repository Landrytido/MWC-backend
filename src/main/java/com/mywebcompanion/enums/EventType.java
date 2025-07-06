package com.mywebcompanion.enums;

public enum EventType {
    EVENT("Événement"),
    TASK_BASED("Événement lié à une tâche");

    private final String displayName;

    EventType(String displayName) {
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