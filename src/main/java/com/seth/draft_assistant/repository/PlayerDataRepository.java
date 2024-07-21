package com.seth.draft_assistant.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlayerDataRepository {

    public void savePlayerData(List<SleeperProjection> playerData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(playerData.get(0));
            System.out.println(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("Inserting player data to db: " + playerData.size() + " records");
    }
}
