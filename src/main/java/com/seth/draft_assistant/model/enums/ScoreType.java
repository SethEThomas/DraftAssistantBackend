package com.seth.draft_assistant.model.enums;

import lombok.Getter;

@Getter
public enum ScoreType {
    COMPLETION_PCT(1, "CompletionPercentage"),
    PASSING_2_PT(2, "Passing2Pt"),
    PASS_ATTEMPT(3, "PassAttempt"),
    PASS_COMPLETION(4,"PassCompletion"),
    PASSING_FIRST_DOWN(5,"PassingFirstDown"),
    PASSING_INTERCEPTION(6,"PassingInterception"),
    PASSING_TD(7,"PassingTd"),
    PASSING_YARD(8, "PassingYard"),
    FUMBLE(9, "Fumble"),
    RECEIVING_2_PT(10, "Receiving2Pt"),
    RECEPTION(11, "Reception"),
    RECEPTION_40_PLUS(12, "Reception40Plus"),
    RECEPTION_FIRST_DOWN(13, "ReceptionFirstDown"),
    RECEIVING_TD(14, "ReceivingTd"),
    RECEIVING_YARD(15, "ReceivingYard"),
    RUSHING_2_PT(16, "Rushing2Pt"),
    RUSHING_ATTEMPT(17, "RushingAttempt"),
    RUSHING_FIRST_DOWN(18, "RushingFirstDown"),
    RUSHING_TD(19, "RushingTd"),
    RUSHING_YARD(20, "RushingYard"),
    TE_RECEPTION_BONUS(21, "TeReceptionBonus");


    private final int id;
    private final String name;

    ScoreType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ScoreType fromId(int id) {
        for (ScoreType type : ScoreType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with id " + id);
    }

    public static ScoreType fromName(String name) {
        for (ScoreType type : ScoreType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);
    }
}


