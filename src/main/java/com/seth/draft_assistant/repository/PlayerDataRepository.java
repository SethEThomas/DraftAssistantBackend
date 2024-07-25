package com.seth.draft_assistant.repository;

import com.seth.draft_assistant.model.enums.AdpType;
import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.enums.Team;
import com.seth.draft_assistant.model.espn.EspnPlayer;
import com.seth.draft_assistant.model.internal.InternalAdp;
import com.seth.draft_assistant.model.internal.ProjectedStat;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.model.sleeper.SleeperStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.seth.draft_assistant.helpers.StatsHelper.generateSleeperProjectedStats;

@Repository
public class PlayerDataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveSleeperPlayerData(List<SleeperProjection> playerData) {
        for (SleeperProjection player : playerData) {
            Long playerId = insertSleeperPlayer(player);
            boolean isTe = getPositionId(player.getPlayer().getPosition()) == Position.fromName("TE").getId();
            if (playerId != null) {
                upsertAdp(playerId, player.getStats().getAdp());
                upsertProjectedStats(playerId, player.getStats(), isTe);
            }
        }
    }

    public void saveEspnPlayerDataWithRetry(List<EspnPlayer> playerData, int maxRetries) {
        for (EspnPlayer player : playerData) {
            retryInsertEspnPlayerData(player, maxRetries);
        }
    }

    private void retryInsertEspnPlayerData(EspnPlayer player, int retries) {
        while (retries > 0) {
            try {
                Long playerId = getPlayerByName(player.getFirstName(), player.getLastName());
                if (playerId != null) {
                    List<InternalAdp> adps = new ArrayList<>();
                    adps.add(new InternalAdp(DataSource.ESPN, AdpType.STANDARD, player.getStandardAdp()));
                    adps.add(new InternalAdp(DataSource.ESPN, AdpType.PPR, player.getPprAdp()));
                    upsertAdp(playerId, adps);
                    return;
                } else {
                    System.out.printf("Player ID not found for ESPN player: %s %s\n", player.getFirstName(), player.getLastName());
                }
            } catch (Exception e) {
                System.out.printf("Error during ESPN player data insertion for: %s %s\n", player.getFirstName(), player.getLastName());
                e.printStackTrace();
            }

            retries--;
            try {
                long backoffTime = (long) Math.pow(2, 3 - retries); // Exponential backoff
                TimeUnit.SECONDS.sleep(backoffTime);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.out.println("Retry interrupted");
                ie.printStackTrace();
            }
        }
    }

    private Long getPlayerByName(String firstName, String lastName) {
        String sql = "SELECT ID FROM PLAYER WHERE NormalizedName = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{normalizeName(firstName, lastName)}, Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Long insertSleeperPlayer(SleeperProjection playerData) {
        String firstName = playerData.getPlayer().getFirstName();
        String lastName = playerData.getPlayer().getLastName();
        Long playerId = getPlayerByName(firstName, lastName);
        if (playerId != null) return playerId;
        String sql = "INSERT INTO PLAYER(NormalizedName, Position, FirstName, LastName, Team) VALUES (?, ?, ?, ?, ?)";
        int positionId = getPositionId(playerData.getPlayer().getPosition());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, normalizeName(firstName, lastName));
                ps.setInt(2, positionId);
                ps.setString(3, firstName);
                ps.setString(4, lastName);
                ps.setInt(5, getTeamId(playerData.getTeam()));
                return ps;
            }, keyHolder);

            // Retrieve the generated key
            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            System.out.println(String.format("Unable to insert player data for %s %s", firstName, lastName));
            e.printStackTrace();
            return null;
        }
    }

    private void upsertAdp(long playerId, List<InternalAdp> adps) {
        for (InternalAdp adp : adps) {
            String columnName = getSourceTypeColumnName(adp.getDataSource());
            String sql = String.format(
                    "INSERT INTO ADP(`PlayerId`, `ADPType`, `%s`) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE `%s` = VALUES(`%s`)",
                    columnName, columnName, columnName);
            try {
                jdbcTemplate.update(sql,
                        playerId,
                        adp.getAdpType().getId(),
                        adp.getAdp());
            } catch (Exception e) {
                System.out.println(String.format("Unable to insert/update player data for player %d", playerId));
                e.printStackTrace();
            }
        }
    }

    private void upsertProjectedStats(long playerId, SleeperStats stats, boolean isTe){
        List<ProjectedStat> projectedStats = generateSleeperProjectedStats(playerId, stats, isTe);
        for(ProjectedStat stat: projectedStats){
            String sql =
                    "INSERT INTO PROJECTED_STATS(`PlayerId`, `ScoreType`, `ProjectedAmount`) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE `ProjectedAmount` = VALUES(`ProjectedAmount`)";
            try {
                jdbcTemplate.update(sql,
                        playerId,
                        stat.getScoreType().getId(),
                        stat.getProjection());
            } catch (Exception e) {
                System.out.println(String.format("Unable to insert/update player data for player %d", playerId));
                e.printStackTrace();
            }
        }
    }

    private String getSourceTypeColumnName(DataSource dataSource){
        switch(dataSource){
            case SLEEPER:
                return "Sleeper";
            case ESPN:
                return "ESPN";
            case YAHOO:
                return "Yahoo";
            case FANTASY_PROS:
                return "FantasyPros";
            default:
                return "N/A";
        }
    }

    private int getPositionId(String positionName) {
        return Position.fromName(positionName).getId();
    }

    private int getTeamId(String teamAbbreviation) {
        return Team.fromAbbreviation(teamAbbreviation).getId();
    }

    private String normalizeName(String firstName, String lastName){
        return firstName.toLowerCase().replaceAll("[^a-z]", "") +
                lastName.toLowerCase().replaceAll("[^a-z]", "");
    }
}
