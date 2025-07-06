package com.mywebcompanion.enums;

public enum ReminderType {
    EMAIL("Email"),
    IN_APP_NOTIFICATION("Notification in-app");

    private final String displayName;

    ReminderType(String displayName) {
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
