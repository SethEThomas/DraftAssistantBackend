package com.seth.draft_assistant.controllers;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.internal.Player;
import com.seth.draft_assistant.model.internal.requests.PlayerUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.RankUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.TierUpdateRequest;
import com.seth.draft_assistant.service.PlayerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.seth.draft_assistant.helpers.PlayerHelper.getPlayerIds;


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

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long playerId) {
        return ResponseEntity.status(200).body(playerDataService.getPlayer(playerId));
    }

    @PostMapping("/players/update")
    public ResponseEntity<String> updatePlayers(@RequestBody List<PlayerUpdateRequest> request){
        List<Long> playerIds = getPlayerIds(request);
        playerDataService.updatePlayers(request);
        return ResponseEntity.status(201).body("Updating players: " + playerIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    @PostMapping("/players/update-tiers")
    public ResponseEntity<String> updatePlayerTiers(@RequestBody List<TierUpdateRequest> request){
        List<Long> playerIds = getPlayerIds(request);
        playerDataService.updateTiers(request);
        return ResponseEntity.status(201).body("Updating player tiers for players: " + playerIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    @PostMapping("/players/update-ranks")
    public ResponseEntity<String> updatePlayerRanks(@RequestBody List<RankUpdateRequest> request){
        List<Long> playerIds = getPlayerIds(request);
        playerDataService.updateRanks(request);
        return ResponseEntity.status(201).body("Updating player ranks for players: " + playerIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }
}
