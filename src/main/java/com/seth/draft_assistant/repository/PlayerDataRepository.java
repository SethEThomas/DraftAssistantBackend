package com.seth.draft_assistant.repository;

import com.seth.draft_assistant.model.enums.AdpType;
import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.enums.Team;
import com.seth.draft_assistant.model.espn.EspnPlayer;
import com.seth.draft_assistant.model.internal.InternalAdp;
import com.seth.draft_assistant.model.internal.Player;
import com.seth.draft_assistant.model.internal.ProjectedStat;
import com.seth.draft_assistant.model.rotowire.RotowirePlayer;
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

    private static final String ALL_PLAYERS_BASE_QUERY = "WITH PlayerData AS (" +
            "SELECT pl.id, pl.NormalizedName, pl.FirstName, pl.LastName, pl.Age, pl.PositionalDepth, " +
            "pl.Notes, pl.IsSleeper, pl.ECR, po.Name AS Position, te.Name AS TeamName, te.ByeWeek AS ByeWeek, " +
            "sos.StrengthOfSchedule " +
            "FROM PLAYER pl " +
            "JOIN POSITION po ON pl.Position = po.id " +
            "JOIN TEAM te ON pl.Team = te.id " +
            "JOIN STRENGTH_OF_SCHEDULE sos ON sos.Team = te.id AND sos.Position = po.id), " +
            "ADPData AS ( " +
            "SELECT a.PlayerId, at.Name AS AdpTypeName, a.Sleeper, a.ESPN, a.FANTRAX, a.NFFC, a.UNDERDOG " +
            "FROM ADP a " +
            "JOIN ADP_TYPE at ON a.AdpType = at.id) " +
            "SELECT pd.*, ";
    private static final String ALL_PLAYERS_GROUP_BY = "GROUP BY pd.id, pd.NormalizedName, pd.FirstName, pd.LastName, pd.Age, pd.PositionalDepth, pd.Notes, " +
            "pd.IsSleeper, pd.ECR, pd.Position, pd.TeamName, pd.ByeWeek, pd.StrengthOfSchedule";

    public void saveSleeperPlayerData(List<SleeperProjection> playerData) {
        System.out.printf("Saving %s rows of Sleeper data\n", playerData.size());
        for (SleeperProjection player : playerData) {
            Long playerId = insertSleeperPlayer(player);
            boolean isTe = getPositionId(player.getPlayer().getPosition()) == Position.fromName("TE").getId();
            if (playerId != null) {
                upsertAdp(playerId, player.getStats().getAdp());
                upsertProjectedStats(playerId, player.getStats(), isTe);
            }
        }
        System.out.println("Finished saving sleeper data");
    }

    public void saveEspnPlayerDataWithRetry(List<EspnPlayer> playerData, int maxRetries) {
        System.out.printf("Saving %s rows of espn data\n", playerData.size());
        for (EspnPlayer player : playerData) {
            retryInsertEspnPlayerData(player, maxRetries);
        }
        System.out.println("Finished saving ESPN data");
    }

    public void saveRotowireDataWithRetry(List<RotowirePlayer> playerData, int maxRetries){
        System.out.printf("Saving %s rows of rotowire data\n", playerData.size());
        for (RotowirePlayer player : playerData) {
            retryInsertRotowirePlayerData(player, maxRetries);
        }
        System.out.println("Finished saving rotowire data");
    }

    private void retryInsertEspnPlayerData(EspnPlayer player, int retries) {
        if(retries == 0){
            doInsertEspnPlayerData(player);
            return;
        }
        while (retries > 0) {
            if(doInsertEspnPlayerData(player)) return;
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

    private boolean doInsertEspnPlayerData(EspnPlayer player){
        try {
            Long playerId = getPlayerByName(player.getFirstName(), player.getLastName());
            if (playerId != null) {
                List<InternalAdp> adps = new ArrayList<>();
                adps.add(new InternalAdp(DataSource.ESPN, AdpType.STANDARD, player.getStandardAdp()));
                adps.add(new InternalAdp(DataSource.ESPN, AdpType.PPR, player.getPprAdp()));
                upsertAdp(playerId, adps);
                return true;
            } else {
                System.out.printf("Player ID not found for ESPN player: %s %s\n", player.getFirstName(), player.getLastName());
                return false;
            }
        } catch (Exception e) {
            System.out.printf("Error during ESPN player data insertion for: %s %s\n", player.getFirstName(), player.getLastName());
            e.printStackTrace();
            return false;
        }
    }

    private boolean doInsertRotowirePlayerData(RotowirePlayer player){
        System.out.printf("Attempting to insert player data for rotowire player %s %s\n", player.getFirstName(), player.getLastName());
        try {
            Long playerId = getPlayerByName(player.getFirstName(), player.getLastName());
            if (playerId != null) {
                List<InternalAdp> adps = new ArrayList<>();
                adps.add(new InternalAdp(DataSource.FANTRAX, AdpType.STANDARD, Double.parseDouble(player.getFantrax12Standard())));
                adps.add(new InternalAdp(DataSource.FANTRAX, AdpType.PPR, Double.parseDouble(player.getFantrax12Ppr())));
                adps.add(new InternalAdp(DataSource.NFFC, AdpType.PPR, Double.parseDouble(player.getNffc12Ppr())));
                adps.add(new InternalAdp(DataSource.UNDERDOG, AdpType.HALF_PPR, Double.parseDouble(player.getUnderdogHalfPpr())));

                upsertAdp(playerId, adps);
                return true;
            } else {
                System.out.printf("Player ID not found for Rotowire player: %s %s\n", player.getFirstName(), player.getLastName());
                return false;
            }
        } catch (Exception e) {
            System.out.printf("Error during Rotowire player data insertion for: %s %s\n", player.getFirstName(), player.getLastName());
            e.printStackTrace();
            return false;
        }
    }

    private void retryInsertRotowirePlayerData(RotowirePlayer player, int retries) {
        if(retries == 0){
            doInsertRotowirePlayerData(player);
            return;
        }
        while (retries > 0) {
            if(doInsertRotowirePlayerData(player)) return;
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
        return switch (dataSource) {
            case SLEEPER -> "Sleeper";
            case ESPN -> "ESPN";
            case ROTOWIRE -> "Rotowire";
            case FANTRAX -> "Fantrax";
            case NFFC -> "NFFC";
            case UNDERDOG -> "Underdog";
            default -> "N/A";
        };
    }

    public List<Player> getAllPlayers(){
        String sql = buildAllPlayerDataDynamicQuery();
        System.out.println(sql);
        return new ArrayList<>();
    }

    private List<String> getAdpTypes() {
        String sql = "SELECT Name FROM ADP_TYPE";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private List<String> getScoreTypes() {
        String sql = "SELECT Name FROM SCORE_TYPE";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private String buildAllPlayerDataDynamicQuery() {
        List<String> adpTypes = getAdpTypes();
        List<String> scoreTypes = getScoreTypes();
        StringBuilder dynamicQuery = new StringBuilder(ALL_PLAYERS_BASE_QUERY);
        for (String adpType : adpTypes) {
            String quotedAdpType = adpType.replace(" ", "_");
            dynamicQuery.append("MAX(CASE WHEN ad.AdpTypeName = '").append(adpType).append("' THEN ad.Sleeper ELSE NULL END) AS \"Sleeper_").append(quotedAdpType).append("\", ");
            dynamicQuery.append("MAX(CASE WHEN ad.AdpTypeName = '").append(adpType).append("' THEN ad.ESPN ELSE NULL END) AS \"ESPN_").append(quotedAdpType).append("\", ");
            dynamicQuery.append("MAX(CASE WHEN ad.AdpTypeName = '").append(adpType).append("' THEN ad.FANTRAX ELSE NULL END) AS \"FANTRAX_").append(quotedAdpType).append("\", ");
            dynamicQuery.append("MAX(CASE WHEN ad.AdpTypeName = '").append(adpType).append("' THEN ad.NFFC ELSE NULL END) AS \"NFFC_").append(quotedAdpType).append("\", ");
            dynamicQuery.append("MAX(CASE WHEN ad.AdpTypeName = '").append(adpType).append("' THEN ad.UNDERDOG ELSE NULL END) AS \"UNDERDOG_").append(quotedAdpType).append("\", ");
        }
        for (String scoreType : scoreTypes) {
            String quotedScoreType = scoreType.replace(" ", "_");
            dynamicQuery.append("MAX(CASE WHEN st.Name = '").append(scoreType).append("' THEN ps.ProjectedAmount ELSE NULL END) AS \"").append(quotedScoreType).append("_PROJECTED_AMOUNT\", ");
            dynamicQuery.append("MAX(CASE WHEN st.Name = '").append(scoreType).append("' THEN ps.ProjectedAmount * st.PointValue ELSE NULL END) AS \"").append(quotedScoreType).append("_PROJECTED_POINTS\", ");
        }
        dynamicQuery.setLength(dynamicQuery.length() - 2);
        dynamicQuery.append(" FROM PlayerData pd ")
                .append("LEFT JOIN ADPData ad ON pd.id = ad.PlayerId ")
                .append("LEFT JOIN PROJECTED_STATS ps ON pd.id = ps.PlayerId ")
                .append("LEFT JOIN SCORE_TYPE st ON ps.ScoreType = st.id ")
                .append("GROUP BY pd.id, pd.NormalizedName, pd.FirstName, pd.LastName, pd.Age, pd.PositionalDepth, pd.Notes, ")
                .append("pd.IsSleeper, pd.ECR, pd.Position, pd.TeamName, pd.ByeWeek, pd.StrengthOfSchedule ")
                .append("ORDER BY Sleeper_PPR;"); // Adjust ORDER BY as needed

        return dynamicQuery.toString();
    }


    private int getPositionId(String positionName) {
        return Position.fromName(positionName).getId();
    }

    private int getTeamId(String teamAbbreviation) {
        return Team.fromAbbreviation(teamAbbreviation).getId();
    }

    private String normalizeName(String firstName, String lastName){
        return normalize(firstName) + normalize(lastName);
    }

    private String normalize(String name){
        return name.toLowerCase().replaceAll("jr.","").replaceAll("sr.","").replaceAll("III","").replaceAll("[^a-z]", "");
    }
}
