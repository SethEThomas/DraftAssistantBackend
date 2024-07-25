package com.seth.draft_assistant.controllers;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.internal.Player;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.service.PlayerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api")
public class PlayerDataController {

    @Autowired
    private PlayerDataService playerDataService;

    @PostMapping("/load-player-data")
    public ResponseEntity<String> loadPlayerData(@RequestParam DataSource[] sources) {
        playerDataService.fetchDataFromSources(sources);
        String sourcesMessage = Stream.of(sources)
                .map(DataSource::name)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(201).body("Fetching player data from " + sourcesMessage);
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getPlayers() {
        return ResponseEntity.status(200).body(playerDataService.getAllPlayers());
    }
}
