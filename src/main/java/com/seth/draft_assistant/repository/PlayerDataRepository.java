package com.seth.draft_assistant.repository;

import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlayerDataRepository {

    public void savePlayerData(List<SleeperProjection> playerData) {
        // Implement the actual data saving logic here
        System.out.println("Inserting player data to db: " + playerData.size() + " records");
    }
}
