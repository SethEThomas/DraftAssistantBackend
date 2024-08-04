package com.seth.draft_assistant.model.internal.requests;

import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.internal.interfaces.Identifiable;
import lombok.Data;

@Data
public class RankUpdateRequest implements Identifiable {
    private Long playerId;
    private Position rankType;
    private Integer rank;

    @Override
    public Long getId() {
        return this.playerId;
    }
}
