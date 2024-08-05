package com.seth.draft_assistant.helpers;

import com.seth.draft_assistant.model.enums.ScoreType;
import com.seth.draft_assistant.model.internal.scoring.ProjectedStat;
import com.seth.draft_assistant.model.sleeper.SleeperStats;

import java.util.ArrayList;
import java.util.List;

public class StatsHelper {
    public static List<ProjectedStat> generateSleeperProjectedStats(long playerId, SleeperStats stats, boolean isTe){
        List<ProjectedStat> returnList = new ArrayList<>();
        returnList.add(new ProjectedStat(playerId, ScoreType.COMPLETION_PCT, stats.getCmpPct()));
        returnList.add(new ProjectedStat(playerId, ScoreType.PASSING_2_PT, stats.getPass2pt()));
        returnList.add(new ProjectedStat(playerId, ScoreType.PASS_ATTEMPT, stats.getPassAtt()));
        returnList.add(new ProjectedStat(playerId, ScoreType.PASSING_FIRST_DOWN, stats.getPassFd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.PASSING_INTERCEPTION, stats.getPassInt()));
        returnList.add(new ProjectedStat(playerId, ScoreType.PASSING_TD, stats.getPassTd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.PASSING_YARD, stats.getPassYd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.FUMBLE, stats.getFumLost()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RECEIVING_2_PT, stats.getPass2pt()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RECEPTION, stats.getRec()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RECEPTION_40_PLUS, stats.getRec40p()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RECEPTION_FIRST_DOWN, stats.getRecFd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RECEIVING_TD, stats.getRecTd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RECEIVING_YARD, stats.getRecYd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RUSHING_2_PT, stats.getRush2pt()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RUSHING_ATTEMPT, stats.getRushAtt()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RUSHING_FIRST_DOWN, stats.getRushFd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RUSHING_TD, stats.getRushTd()));
        returnList.add(new ProjectedStat(playerId, ScoreType.RUSHING_YARD, stats.getRushYd()));
        if(isTe){
            returnList.add(new ProjectedStat(playerId, ScoreType.TE_RECEPTION_BONUS, stats.getRec()));
        }
        return returnList;
    }
}
