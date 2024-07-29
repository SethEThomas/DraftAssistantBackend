package com.seth.draft_assistant.helpers;

import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.enums.Team;

public class DbHelper {
    public static int getPositionId(String positionName) {
        return Position.fromName(positionName).getId();
    }

    public static int getTeamId(String teamAbbreviation) {
        return Team.fromAbbreviation(teamAbbreviation).getId();
    }

    public static String normalizeName(String firstName, String lastName){
        return normalize(firstName) + normalize(lastName);
    }

    public static String normalize(String name){
        return name.toLowerCase().replaceAll("jr.","").replaceAll("sr.","").replaceAll("III","").replaceAll("[^a-z]", "");
    }
}
