package com.seth.draft_assistant.controllers;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.service.PlayerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api")
public class PlayerDataController {

    @Autowired
    private PlayerDataService playerDataService;

    @PostMapping("/load-player-data")
    public ResponseEntity<String> loadPlayerData(@RequestParam DataSource[] sources) {
        CompletableFuture<String> fetchResult = playerDataService.fetchDataFromSources(sources);

        // Immediately return response
        return ResponseEntity.status(202).body("Fetching player data from " + fetchResult.join());
    }
}
