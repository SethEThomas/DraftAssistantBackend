package com.seth.draft_assistant.helpers;

import com.seth.draft_assistant.model.internal.interfaces.Identifiable;
import com.seth.draft_assistant.model.internal.requests.PlayerUpdateRequest;

import java.util.ArrayList;
import java.util.List;

public class PlayerHelper {
    public static <T extends Identifiable> List<Long> getPlayerIds(List<T> requests) {
        List<Long> returnList = new ArrayList<>();
        for (T item : requests) {
            returnList.add(item.getId());
        }
        return returnList;
    }
}
