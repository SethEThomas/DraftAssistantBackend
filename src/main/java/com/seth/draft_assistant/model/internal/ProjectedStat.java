package com.seth.draft_assistant.model.internal;

import com.seth.draft_assistant.model.enums.ScoreType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProjectedStat {
    private long playerId;

    private ScoreType scoreType;

    private double projection;
}
