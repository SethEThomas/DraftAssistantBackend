package com.seth.draft_assistant.model.internal.requests;

import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.internal.interfaces.Identifiable;
import lombok.Data;

@Data
public class TierUpdateRequest implements Identifiable {
    private Long playerId;
    private Position tierType;
    private Integer tier;

    @Override
    public Long getId() {
        return this.playerId;
    }
}
