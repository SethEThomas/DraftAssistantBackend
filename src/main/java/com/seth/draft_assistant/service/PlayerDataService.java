package com.seth.draft_assistant.service;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.repository.PlayerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.seth.draft_assistant.Constants.CURRENT_YEAR;
import static com.seth.draft_assistant.Constants.SLEEPER_ADP_URL_TEMPLATE;

@Service
public class PlayerDataService {

    @Autowired
    private PlayerDataRepository playerDataRepository;

    @Async
    public CompletableFuture<List<SleeperProjection>> fetchDataFromSource(DataSource source) {
        List<SleeperProjection> projections = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        switch (source) {
            case SLEEPER:
                String sleeperUrl = String.format(SLEEPER_ADP_URL_TEMPLATE, CURRENT_YEAR);
                ResponseEntity<SleeperProjection[]> sleeperResponse = restTemplate.getForEntity(sleeperUrl, SleeperProjection[].class);
                SleeperProjection[] sleeperProjections = sleeperResponse.getBody();
                if (sleeperProjections != null) {
                    projections.addAll(List.of(sleeperProjections));
                }
                break;
            case YAHOO:
                // Fetch data from Yahoo
                break;
            case ESPN:
                // Fetch data from ESPN
                break;
            case FANTASY_PROS:
                // Fetch data from Fantasy Pros
                break;
            default:
                throw new IllegalArgumentException("Unsupported data source: " + source);
        }
        return CompletableFuture.completedFuture(projections);
    }

    public CompletableFuture<String> fetchDataFromSources(DataSource[] sources) {
        List<CompletableFuture<List<SleeperProjection>>> futures;
        String sourcesMessage;

        if (Stream.of(sources).anyMatch(source -> source == DataSource.ALL)) {
            futures = Stream.of(DataSource.values())
                    .filter(source -> source != DataSource.ALL)
                    .map(this::fetchDataFromSource)
                    .collect(Collectors.toList());

            sourcesMessage = "all sources";
        } else {
            futures = Stream.of(sources)
                    .map(this::fetchDataFromSource)
                    .collect(Collectors.toList());

            sourcesMessage = Stream.of(sources)
                    .map(DataSource::name)
                    .collect(Collectors.joining(", "));
        }

        CompletableFuture<List<SleeperProjection>> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .flatMap(future -> future.join().stream())
                        .collect(Collectors.toList()));

        return allOf.thenApply(allProjections -> {
            // Insert the aggregated data into the database
            insertPlayerDataToDb(allProjections);
            return sourcesMessage;
        });
    }

    private void insertPlayerDataToDb(List<SleeperProjection> playerData) {
        // Delegate to the repository to save the data
        playerDataRepository.savePlayerData(playerData);
    }
}
