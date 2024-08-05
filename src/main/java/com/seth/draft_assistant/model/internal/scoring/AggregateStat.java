package com.seth.draft_assistant.model.internal.scoring;

import lombok.Data;

@Data
public class AggregateStat {
    public AggregateStat(SingleStat completionPct,
                         SingleStat passing2Pt,
                         SingleStat passAttempt,
                         SingleStat passCompletion,
                         SingleStat passingFirstDown,
                         SingleStat passingInterception,
                         SingleStat passingTd,
                         SingleStat passingYard,
                         SingleStat fumble,
                         SingleStat receiving2Pt,
                         SingleStat reception,
                         SingleStat reception40Plus,
                         SingleStat receptionFirstDown,
                         SingleStat receivingTd,
                         SingleStat receivingYard,
                         SingleStat rushing2Pt,
                         SingleStat rushingAttempt,
                         SingleStat rushingFirstDown,
                         SingleStat rushingTd,
                         SingleStat rushingYard,
                         SingleStat teReceptionBonus){
        this.completionPct = completionPct;
        this.passing2Pt = passing2Pt;
        this.passAttempt = passAttempt;
        this.passCompletion = passCompletion;
        this.passingFirstDown = passingFirstDown;
        this.passingInterception = passingInterception;
        this.passingTd = passingTd;
        this.passingYard = passingYard;
        this.fumble = fumble;
        this.receiving2Pt = receiving2Pt;
        this.reception = reception;
        this.reception40Plus = reception40Plus;
        this.receptionFirstDown = receptionFirstDown;
        this.receivingTd = receivingTd;
        this.receivingYard = receivingYard;
        this.rushing2Pt = rushing2Pt;
        this.rushingAttempt = rushingAttempt;
        this.rushingFirstDown = rushingFirstDown;
        this.rushingTd = rushingTd;
        this.rushingYard = rushingYard;
        this.teReceptionBonus = teReceptionBonus;
        this.totalProjectedPoints = completionPct.getProjectedPoints() +
                passing2Pt.getProjectedPoints() +
                passAttempt.getProjectedPoints() +
                passCompletion.getProjectedAmount() +
                passingFirstDown.getProjectedPoints() +
                passingInterception.getProjectedPoints() +
                passingTd.getProjectedPoints() +
                passingYard.getProjectedPoints() +
                fumble.getProjectedPoints() +
                receiving2Pt.getProjectedPoints() +
                reception.getProjectedPoints() +
                reception40Plus.getProjectedPoints() +
                receptionFirstDown.getProjectedPoints() +
                receivingTd.getProjectedPoints() +
                receivingYard.getProjectedPoints() +
                rushing2Pt.getProjectedPoints() +
                rushingAttempt.getProjectedPoints() +
                rushingFirstDown.getProjectedPoints() +
                rushingTd.getProjectedPoints() +
                rushingYard.getProjectedPoints() +
                teReceptionBonus.getProjectedPoints();
    }
    private SingleStat completionPct;
    private SingleStat passing2Pt;
    private SingleStat passAttempt;
    private SingleStat passCompletion;
    private SingleStat passingFirstDown;
    private SingleStat passingInterception;
    private SingleStat passingTd;
    private SingleStat passingYard;
    private SingleStat fumble;
    private SingleStat receiving2Pt;
    private SingleStat reception;
    private SingleStat reception40Plus;
    private SingleStat receptionFirstDown;
    private SingleStat receivingTd;
    private SingleStat receivingYard;
    private SingleStat rushing2Pt;
    private SingleStat rushingAttempt;
    private SingleStat rushingFirstDown;
    private SingleStat rushingTd;
    private SingleStat rushingYard;
    private SingleStat teReceptionBonus;
    private double totalProjectedPoints;
}
