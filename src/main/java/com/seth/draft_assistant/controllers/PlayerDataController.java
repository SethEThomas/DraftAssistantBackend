package com.seth.draft_assistant.controllers;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static com.seth.draft_assistant.Constants.CURRENT_YEAR;
import static com.seth.draft_assistant.Constants.SLEEPER_ADP_URL_TEMPLATE;


@RestController
@RequestMapping("/api")
public class PlayerDataController {

    @PostMapping("/load-player-data")
    public ResponseEntity<SleeperProjection[]> loadPlayerData(@RequestParam DataSource[] sources) {
        String url = String.format(SLEEPER_ADP_URL_TEMPLATE, CURRENT_YEAR);
        RestTemplate restTemplate = new RestTemplate();

        // Handle the sources parameter
        for (DataSource source : sources) {
            switch (source) {
                case ALL:
                    // Fetch data from all sources
                    break;
                case SLEEPER:
                    // Fetch data from Sleeper
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported data source: " + source);
            }
        }

        ResponseEntity<SleeperProjection[]> response = restTemplate.getForEntity(url, SleeperProjection[].class);
        SleeperProjection[] projections = response.getBody();

        for (SleeperProjection projection : projections) {
            System.out.println(projection.getPlayer().getFirstName() + " " + projection.getPlayer().getLastName());
        }

        return ResponseEntity.ok(projections);
    }
}
