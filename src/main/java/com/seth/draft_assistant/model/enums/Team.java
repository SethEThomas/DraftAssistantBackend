package com.seth.draft_assistant.model.enums;

import lombok.Getter;

@Getter
public enum Team {
    ARIZONA_CARDINALS(1, "ARI"),
    ATLANTA_FALCONS(2, "ATL"),
    BALTIMORE_RAVENS(3, "BAL"),
    BUFFALO_BILLS(4, "BUF"),
    CAROLINA_PANTHERS(5, "CAR"),
    CHICAGO_BEARS(6, "CHI"),
    CINCINNATI_BENGALS(7, "CIN"),
    CLEVELAND_BROWNS(8, "CLE"),
    DALLAS_COWBOYS(9, "DAL"),
    DENVER_BRONCOS(10, "DEN"),
    DETROIT_LIONS(11, "DET"),
    GREEN_BAY_PACKERS(12, "GB"),
    HOUSTON_TEXANS(13, "HOU"),
    INDIANAPOLIS_COLTS(14, "IND"),
    JACKSONVILLE_JAGUARS(15, "JAC"),
    KANSAS_CITY_CHIEFS(16, "KC"),
    LOS_ANGELES_CHARGERS(17, "LAC"),
    LOS_ANGELES_RAM(18, "LAR"),
    LAS_VEGAS_RAIDERS(19, "LV"),
    MIAMI_DOLPHINS(20, "MIA"),
    MINNESOTA_VIKINGS(21, "MIN"),
    NEW_ENGLAND_PATRIOTS(22, "NE"),
    NEW_ORLEANS_SAINTS(23, "NO"),
    NEW_YORK_GIANTS(24, "NYG"),
    NEW_YORK_JETS(25, "NYJ"),
    PHILADELPHIA_EAGLES(26, "PHI"),
    PITTSBURGH_STEELERS(27, "PIT"),
    SEATTLE_SEAHAWKS(28, "SEA"),
    SAN_FRANCISCO_49ERS(29, "SF"),
    TAMPA_BAY_BUCCANEERS(30, "TB"),
    TENNESSEE_TITANS(31, "TEN"),
    WASHINGTON_COMMANDERS(32, "WAS"),
    FREE_AGENT(33, "UDFA");

    private final int id;
    private final String abbreviation;

    Team(int id, String abbreviation) {
        this.id = id;
        this.abbreviation = abbreviation;
    }

    public static Team fromId(int id) {
        for (Team team : Team.values()) {
            if (team.id == id) {
                return team;
            }
        }
        return FREE_AGENT;
    }

    public static Team fromAbbreviation(String abbreviation) {
        for (Team team : Team.values()) {
            if (team.abbreviation.equalsIgnoreCase(abbreviation)) {
                return team;
            }
        }
        return FREE_AGENT;
    }
}

