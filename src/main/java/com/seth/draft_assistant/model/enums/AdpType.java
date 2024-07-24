package com.seth.draft_assistant.model.enums;

import lombok.Getter;

@Getter
public enum AdpType {
    STANDARD(1, "Standard"),
    HALF_PPR(2, "Half PPR"),
    PPR(3, "PPR"),
    TWO_QB(4,"2QB"),
    DYNASTY_STANDARD(5,"Dynasty Standard"),
    DYNASTY_HALF_PPR(6,"Dynasty Half PPR"),
    DYNASTY_PPR(7,"Dynasty PPR"),
    DYNASTY_2_QB(8, "Dynasty 2QB");


    private final int id;
    private final String name;

    AdpType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static AdpType fromId(int id) {
        for (AdpType type : AdpType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with id " + id);
    }

    public static AdpType fromName(String name) {
        for (AdpType type : AdpType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);
    }
}

