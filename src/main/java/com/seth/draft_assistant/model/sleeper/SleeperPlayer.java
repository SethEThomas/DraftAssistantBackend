package com.seth.draft_assistant.model.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SleeperPlayer {
    @JsonProperty("years_exp")
    private int yearsExp;

    @JsonProperty("team_abbr")
    private String teamAbbr;

    private String team;
    private String position;

    @JsonProperty("news_updated")
    private long newsUpdated;

    private SleeperMetadata metadata;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("injury_status")
    private String injuryStatus;

    @JsonProperty("injury_start_date")
    private String injuryStartDate;

    @JsonProperty("injury_notes")
    private String injuryNotes;

    @JsonProperty("injury_body_part")
    private String injuryBodyPart;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("fantasy_positions")
    private String[] fantasyPositions;
}
