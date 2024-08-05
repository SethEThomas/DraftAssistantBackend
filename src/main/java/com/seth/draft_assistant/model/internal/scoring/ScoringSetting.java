package com.seth.draft_assistant.model.internal.scoring;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ScoringSetting {
    private int id;
    private String name;
    private String displayName;
    private double pointValue;
}
