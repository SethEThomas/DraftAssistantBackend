package com.seth.draft_assistant.model.sleeper;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SleeperMetadata {
    @JsonProperty("rookie_year")
    private String rookieYear;

    @JsonProperty("channel_id")
    private String channelId;
}
