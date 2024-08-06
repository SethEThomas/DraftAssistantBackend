package com.seth.draft_assistant.controllers;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.internal.player.Player;
import com.seth.draft_assistant.model.internal.requests.PlayerUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.ScoringSettingUpdateRequest;
import com.seth.draft_assistant.model.internal.scoring.ScoringSetting;
import com.seth.draft_assistant.service.PlayerDataService;
import com.seth.draft_assistant.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.seth.draft_assistant.helpers.PlayerHelper.getPlayerIds;


@RestController
@RequestMapping("/api/scoring")
public class ScoringController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/settings")
    public ResponseEntity<List<ScoringSetting>> getScoringSettings() {
        return ResponseEntity.status(200).body(scoreService.getScoringSettings());
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePlayers(
            @RequestBody List<ScoringSettingUpdateRequest> request,
            @RequestParam(name = "returnPlayers", defaultValue = "false") boolean returnPlayers) {
        if (returnPlayers) {
            List<Player> players = scoreService.updateScoringSettingsAndReturnPlayers(request);
            return ResponseEntity.ok(players);
        } else {
            scoreService.updateScoringSettings(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Updating scoring settings"); // 201 Created with message
        }
    }
}
