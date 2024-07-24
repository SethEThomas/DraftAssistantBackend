package com.seth.draft_assistant.model.enums;

import lombok.Getter;

@Getter
public enum Position {
    OVERALL(1, "OVERALL"),
    QB(2, "QB"),
    WR(3, "WR"),
    TE(4, "TE"),
    RB(5, "RB"),
    UNKNOWN(6, "UNKNOWN");

    private final int id;
    private final String name;

    Position(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Position fromId(int id) {
        for (Position position : Position.values()) {
            if (position.id == id) {
                return position;
            }
        }
        return UNKNOWN;
    }

    public static Position fromName(String name) {
        for (Position position : Position.values()) {
            if (position.name.equalsIgnoreCase(name)) {
                return position;
            }
        }
        return UNKNOWN;
    }
}
