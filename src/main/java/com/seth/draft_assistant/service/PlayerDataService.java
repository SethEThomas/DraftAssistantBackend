package com.seth.draft_assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.model.espn.EspnPlayer;
import com.seth.draft_assistant.repository.PlayerDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.seth.draft_assistant.Constants.*;

@Slf4j
@Service
public class PlayerDataService {

    @Autowired
    private PlayerDataRepository playerDataRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public CompletableFuture<Void> fetchSleeperDataFromSource() {
        return CompletableFuture.runAsync(() -> {
            String sleeperUrl = String.format(SLEEPER_ADP_URL_TEMPLATE, CURRENT_YEAR);
            ResponseEntity<SleeperProjection[]> response = restTemplate.getForEntity(sleeperUrl, SleeperProjection[].class);
            SleeperProjection[] sleeperProjections = response.getBody();
            List<SleeperProjection> projections = (sleeperProjections != null) ? List.of(sleeperProjections) : Collections.emptyList();
            if (!projections.isEmpty()) {
                playerDataRepository.saveSleeperPlayerData(projections);
            }
        });
    }

    @Async
    public CompletableFuture<Void> fetchEspnDataFromSource() {
        return CompletableFuture.supplyAsync(() -> {
            String espnUrl = String.format(ESPN_ADP_URL_TEMPLATE, CURRENT_YEAR);
            HttpHeaders headers = new HttpHeaders();
            headers.set(ESPN_FANTASY_FILTER_HEADER, ESPN_FANTASY_FILTER_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(espnUrl, HttpMethod.GET, entity, String.class);
            String json = response.getBody();
            List<EspnPlayer> players = parseEspnPlayers(json);
            return players;
        }).thenAccept(players -> {
            if (!players.isEmpty()) {
                playerDataRepository.saveEspnPlayerDataWithRetry(players, 1);
            }
        });
    }

    private List<EspnPlayer> parseEspnPlayers(String json) {
        List<EspnPlayer> players = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode playersNode = rootNode.path("players");

            if (playersNode.isArray()) {
                for (JsonNode playerNode : playersNode) {
                    JsonNode player = playerNode.path("player");
                    String firstName = player.path("firstName").asText();
                    String lastName = player.path("lastName").asText();

                    EspnPlayer espnPlayer = new EspnPlayer();
                    espnPlayer.setFirstName(firstName);
                    espnPlayer.setLastName(lastName);

                    JsonNode draftRanksByType = player.path("draftRanksByRankType");
                    Iterator<String> rankTypes = draftRanksByType.fieldNames();

                    while (rankTypes.hasNext()) {
                        String rankType = rankTypes.next();
                        int rank = draftRanksByType.path(rankType).path("rank").asInt();

                        if ("STANDARD".equals(rankType)) {
                            espnPlayer.setStandardAdp(rank);
                        } else if ("PPR".equals(rankType)) {
                            espnPlayer.setPprAdp(rank);
                        }
                    }

                    players.add(espnPlayer);
                }
            }
        } catch (IOException e) {
            System.out.println("Error parsing ESPN player data");
            e.printStackTrace();
        }
        return players;
    }

    @Async
    public CompletableFuture<Void> fetchDataFromSources(DataSource[] sources) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        if (Stream.of(sources).anyMatch(source -> source == DataSource.ALL)) {
            futures.add(fetchSleeperDataFromSource());
            futures.add(fetchEspnDataFromSource());
        } else {
            for (DataSource source : sources) {
                switch (source) {
                    case SLEEPER:
                        futures.add(fetchSleeperDataFromSource());
                        break;
                    case ESPN:
                        futures.add(fetchEspnDataFromSource());
                        break;
                    // Add other sources here as needed
                    default:
                        System.out.println("Unsupported data source: " + source);
                        break;
                }
            }
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allFutures.exceptionally(ex -> {
            System.out.println("Error fetching data: ");
            ex.printStackTrace();
            return null;
        });
    }
}
