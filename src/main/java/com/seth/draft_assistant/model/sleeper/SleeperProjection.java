package com.seth.draft_assistant.model.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SleeperProjection {
    private SleeperPlayer player;
    private String opponent;
    private String company;
    private String team;

    @JsonProperty("player_id")
    private String playerId;

    @JsonProperty("updated_at")
    private long updatedAt;

    @JsonProperty("game_id")
    private String gameId;

    private String sport;

    @JsonProperty("season_type")
    private String seasonType;

    private String season;
    private String week;

    @JsonProperty("last_modified")
    private long lastModified;

    private String category;
    private SleeperStats stats;
    private String date;
}
