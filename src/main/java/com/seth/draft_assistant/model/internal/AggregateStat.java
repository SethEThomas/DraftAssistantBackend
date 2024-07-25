package com.seth.draft_assistant.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AggregateStat {
    private double completionPct;
    private double passing2Pt;
    private double passAttempt;
    private double passCompletion;
    private double passingFirstDown;
    private double passingInterception;
    private double passingTd;
    private double passingYard;
    private double fumble;
    private double receiving2Pt;
    private double reception;
    private double reception40Plus;
    private double receptionFirstDown;
    private double receivingTd;
    private double receivingYard;
    private double rushing2Pt;
    private double rushingAttempt;
    private double rushingFirstDown;
    private double rushingTd;
    private double rushingYard;
    private double teReceptionBonus;
}
