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
    private int age;
    private int positionalDepth;
    private String notes;
    private Boolean isSleeper;
    private double ecr;
    private String position;
    private String teamName;
    private String teamAbbreviation;
    private int byeWeek;
    private int strengthOfSchedule;
    private int overallTier;
    private int positionalTier;
    private AggregateAdp adp;
    private AggregateStat stats;
}
