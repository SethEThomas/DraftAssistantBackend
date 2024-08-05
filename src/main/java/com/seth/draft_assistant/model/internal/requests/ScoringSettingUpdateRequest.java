package com.seth.draft_assistant.model.internal.requests;
import com.seth.draft_assistant.model.internal.interfaces.Identifiable;
import lombok.Data;

@Data
public class ScoringSettingUpdateRequest {
    private int scoringSettingId;
    private double pointValue;
}
