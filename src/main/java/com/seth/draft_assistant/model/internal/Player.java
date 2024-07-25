package com.seth.draft_assistant.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private Long id;
    private String normalizedName;
    private String firstName;
    private String lastName;
    private String teamName;
    private String teamAbbreviation;
    private int positionalDepth;
    private String notes;
    private Boolean isSleeper;
    private double ecr;
    private String position;
    private AggregateStat stats;
    private AggregateAdp adp;
    private int strengthOfSchedule;
    private int overallTier;
    private int positionalTier;
}
