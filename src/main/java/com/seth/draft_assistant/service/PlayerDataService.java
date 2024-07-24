package com.seth.draft_assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.model.espn.EspnPlayer; // Assuming this is the correct import
import com.seth.draft_assistant.repository.PlayerDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.seth.draft_assistant.Constants.CURRENT_YEAR;
import static com.seth.draft_assistant.Constants.SLEEPER_ADP_URL_TEMPLATE;

@Slf4j
@Service
public class PlayerDataService {

    @Autowired
    private PlayerDataRepository playerDataRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public CompletableFuture<List<SleeperProjection>> fetchSleeperDataFromSource() {
        String sleeperUrl = String.format(SLEEPER_ADP_URL_TEMPLATE, CURRENT_YEAR);
        ResponseEntity<SleeperProjection[]> response = restTemplate.getForEntity(sleeperUrl, SleeperProjection[].class);
        SleeperProjection[] sleeperProjections = response.getBody();
        List<SleeperProjection> projections = (sleeperProjections != null) ? List.of(sleeperProjections) : Collections.emptyList();
        return CompletableFuture.completedFuture(projections);
    }

    @Async
    public CompletableFuture<List<EspnPlayer>> fetchEspnDataFromSource() {
        String espnUrl = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/2024/segments/0/leaguedefaults/3?scoringPeriodId=0&view=kona_player_info";
        ResponseEntity<String> response = restTemplate.getForEntity(espnUrl, String.class);
        String json = response.getBody();
        List<EspnPlayer> players = parseEspnPlayers(json);
        return CompletableFuture.completedFuture(players);
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
            log.error("Error parsing ESPN player data", e);
        }
        return players;
    }

    @Async
    public void fetchDataFromSources(DataSource[] sources) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

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
                        log.warn("Unsupported data source: " + source);
                        break;
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    List<SleeperProjection> sleeperData = null;
                    List<EspnPlayer> espnData = null;

                    for (CompletableFuture<?> future : futures) {
                        if (future.join() instanceof List) {
                            List<?> result = (List<?>) future.join();
                            if (!result.isEmpty()) {
                                if (result.get(0) instanceof SleeperProjection) {
                                    sleeperData = (List<SleeperProjection>) result;
                                } else if (result.get(0) instanceof EspnPlayer) {
                                    espnData = (List<EspnPlayer>) result;
                                }
                            }
                        }
                    }

                    if (sleeperData != null) {
                        insertSleeperPlayerDataToDb(sleeperData);
                    }

                    if (espnData != null) {
                        insertEspnPlayerDataToDb(espnData);
                    }
                })
                .exceptionally(ex -> {
                    log.error("Error fetching data: " + ex.getMessage());
                    return null;
                });
    }

    private void insertSleeperPlayerDataToDb(List<SleeperProjection> playerData) {
        playerDataRepository.saveSleeperPlayerData(playerData);
    }

     private void insertEspnPlayerDataToDb(List<EspnPlayer> playerData) {
         playerDataRepository.saveEspnPlayerData(playerData);
     }
}

