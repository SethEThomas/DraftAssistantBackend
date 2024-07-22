package com.seth.draft_assistant.model.internal;

import com.seth.draft_assistant.model.enums.AdpType;
import com.seth.draft_assistant.model.enums.DataSource;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalAdp {
    private DataSource dataSource;

    private AdpType adpType;

    private double adp;
}
