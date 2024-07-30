package com.seth.draft_assistant.repository;

import com.seth.draft_assistant.model.enums.*;
import com.seth.draft_assistant.model.espn.EspnPlayer;
import com.seth.draft_assistant.model.internal.*;
import com.seth.draft_assistant.model.internal.requests.PlayerUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.TierUpdateRequest;
import com.seth.draft_assistant.model.rotowire.RotowirePlayer;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.model.sleeper.SleeperStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.seth.draft_assistant.helpers.DbHelper.*;
import static com.seth.draft_assistant.helpers.StatsHelper.generateSleeperProjectedStats;

@Repository
public class PlayerDataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ALL_PLAYERS_BASE_QUERY = "WITH PlayerData AS (" +
            "SELECT pl.id, pl.NormalizedName, pl.FirstName, pl.LastName, pl.Age, pl.PositionalDepth, " +
            "pl.Notes, pl.IsSleeper, pl.ECR, po.Name AS Position, te.Name AS TeamName, te.Abbreviation AS TeamAbbreviation, te.ByeWeek AS ByeWeek, " +
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
    private static final String ALL_PLAYERS_GROUP_BY = "GROUP BY pd.id, " +
            "pd.NormalizedName, " +
            "pd.FirstName, " +
            "pd.LastName, pd.Age, " +
            "pd.PositionalDepth, " +
            "pd.Notes, " +
            "pd.IsSleeper, " +
            "pd.ECR, " +
            "pd.Position, " +
            "pd.TeamName, " +
            "pd.ByeWeek, " +
            "pd.StrengthOfSchedule ) " +
            "as FullPlayerStats ";
    private static final String TIER_UPDATE_SQL = "INSERT INTO TIER (PlayerId, Position, Tier) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Position = VALUES(Position), Tier = VALUES(Tier)";

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

    public void updateTiers(List<TierUpdateRequest> requests) {
        String sql = TIER_UPDATE_SQL;
        jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, argument) -> {
            ps.setLong(1, argument.getPlayerId());
            ps.setInt(2, argument.getTierType().getId());
            ps.setInt(3, argument.getTier());
        });
    }

    public void updatePlayers(List<PlayerUpdateRequest> playerUpdateRequests) {
        List<String> sqlStatements = new ArrayList<>();
        List<List<Object>> paramsList = new ArrayList<>();

        for (PlayerUpdateRequest updateRequest : playerUpdateRequests) {
            String sql = buildUpdateSql(updateRequest);
            List<Object> params = buildUpdateParams(updateRequest);
            sqlStatements.add(sql);
            paramsList.add(params);
        }

        for (int i = 0; i < sqlStatements.size(); i++) {
            final int index = i;
            String sql = sqlStatements.get(i);
            List<Object> params = paramsList.get(i);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    List<Object> params = paramsList.get(index);
                    for (int j = 0; j < params.size(); j++) {
                        ps.setObject(j + 1, params.get(j));
                    }
                }

                @Override
                public int getBatchSize() {
                    return paramsList.size();
                }
            });
        }
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
        String sql = buildPlayerDataDynamicQuery(null);
        return jdbcTemplate.query(sql, new PlayerRowMapper());
    }

    public Player getPlayer(Long playerId){
        String sql = buildPlayerDataDynamicQuery(playerId);
        return jdbcTemplate.query(sql, new PlayerRowMapper()).get(0);
    }

    private List<String> getTypes(TypeTable typeTable){
        String sql = String.format("SELECT Name FROM %s",typeTable.name());
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private String buildPlayerDataDynamicQuery(Long playerId) {
        List<String> adpTypes = getTypes(TypeTable.ADP_TYPE);
        List<String> scoreTypes = getTypes(TypeTable.SCORE_TYPE);
        List<String> tierTypes = getTypes(TypeTable.POSITION);

        StringBuilder dynamicQuery = new StringBuilder();
        dynamicQuery.append("SELECT * FROM (");
        dynamicQuery.append(ALL_PLAYERS_BASE_QUERY);
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
        for (String tierType : tierTypes) {
            String quotedTierType = tierType.replace(" ", "_");
            dynamicQuery.append("MAX(CASE WHEN po.Name = '").append(tierType).append("' THEN ti.Tier ELSE NULL END) AS \"").append(quotedTierType).append("_TIER\", ");
        }
        dynamicQuery.setLength(dynamicQuery.length() - 2); // Remove the last comma and space

        dynamicQuery.append(" FROM PlayerData pd ")
                .append("LEFT JOIN ADPData ad ON pd.id = ad.PlayerId ")
                .append("LEFT JOIN PROJECTED_STATS ps ON pd.id = ps.PlayerId ")
                .append("LEFT JOIN SCORE_TYPE st ON ps.ScoreType = st.id ")
                .append("LEFT JOIN TIER ti ON pd.id = ti.PlayerId ")
                .append("LEFT JOIN POSITION po ON ti.Position = po.ID ");

        if (playerId != null) {
            dynamicQuery.append("WHERE pd.id = ").append(playerId).append(" ");
        }

        dynamicQuery.append(ALL_PLAYERS_GROUP_BY);
        // Find a correct column to order by
        // For example, if you want to order by "Sleeper_PPR", ensure it exists
        String defaultAdpType = "PPR";
        String quotedDefaultAdpType = defaultAdpType.replace(" ", "_");
        dynamicQuery.append("ORDER BY Sleeper_").append(quotedDefaultAdpType).append(" ASC;");

        return dynamicQuery.toString();
    }

    private String buildUpdateSql(PlayerUpdateRequest updateRequest) {
        StringBuilder sql = new StringBuilder("UPDATE PLAYER SET ");
        boolean first = true;

        if (updateRequest.getAge() != null) {
            if (!first) sql.append(", ");
            sql.append("Age = ?");
            first = false;
        }
        if (updateRequest.getPositionalDepth() != null) {
            if (!first) sql.append(", ");
            sql.append("PositionalDepth = ?");
            first = false;
        }
        if (updateRequest.getNotes() != null) {
            if (!first) sql.append(", ");
            sql.append("Notes = ?");
            first = false;
        }
        if (updateRequest.getIsSleeper() != null) {
            if (!first) sql.append(", ");
            sql.append("IsSleeper = ?");
            first = false;
        }
        if (updateRequest.getEcr() != null) {
            if (!first) sql.append(", ");
            sql.append("ECR = ?");
        }
        sql.append(" WHERE ID = ?");
        return sql.toString();
    }

    private List<Object> buildUpdateParams(PlayerUpdateRequest updateRequest) {
        List<Object> params = new ArrayList<>();

        if (updateRequest.getAge() != null) {
            params.add(updateRequest.getAge());
        }
        if (updateRequest.getPositionalDepth() != null) {
            params.add(updateRequest.getPositionalDepth());
        }
        if (updateRequest.getNotes() != null) {
            params.add(updateRequest.getNotes());
        }
        if (updateRequest.getIsSleeper() != null) {
            params.add(updateRequest.getIsSleeper());
        }
        if (updateRequest.getEcr() != null) {
            params.add(updateRequest.getEcr());
        }
        params.add(updateRequest.getId());
        return params;
    }

    private static class PlayerRowMapper implements RowMapper<Player>{
        @Override
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            AggregateAdp adp = new AggregateAdp(
                    rs.getDouble("Sleeper_Standard"), rs.getDouble("ESPN_Standard"), rs.getDouble("FANTRAX_Standard"),
                    rs.getDouble("NFFC_Standard"), rs.getDouble("UNDERDOG_Standard"), rs.getDouble("Sleeper_Half_PPR"),
                    rs.getDouble("ESPN_Half_PPR"), rs.getDouble("FANTRAX_Half_PPR"), rs.getDouble("NFFC_Half_PPR"),
                    rs.getDouble("UNDERDOG_Half_PPR"), rs.getDouble("Sleeper_PPR"), rs.getDouble("ESPN_PPR"),
                    rs.getDouble("FANTRAX_PPR"), rs.getDouble("NFFC_PPR"), rs.getDouble("UNDERDOG_PPR"),
                    rs.getDouble("Sleeper_2QB"), rs.getDouble("ESPN_2QB"), rs.getDouble("FANTRAX_2QB"),
                    rs.getDouble("NFFC_2QB"), rs.getDouble("UNDERDOG_2QB"), rs.getDouble("Sleeper_Dynasty_Standard"),
                    rs.getDouble("ESPN_Dynasty_Standard"), rs.getDouble("FANTRAX_Dynasty_Standard"),
                    rs.getDouble("NFFC_Dynasty_Standard"), rs.getDouble("UNDERDOG_Dynasty_Standard"),
                    rs.getDouble("Sleeper_Dynasty_Half_PPR"), rs.getDouble("ESPN_Dynasty_Half_PPR"),
                    rs.getDouble("FANTRAX_Dynasty_Half_PPR"), rs.getDouble("NFFC_Dynasty_Half_PPR"),
                    rs.getDouble("UNDERDOG_Dynasty_Half_PPR"), rs.getDouble("Sleeper_Dynasty_PPR"),
                    rs.getDouble("ESPN_Dynasty_PPR"), rs.getDouble("FANTRAX_Dynasty_PPR"),
                    rs.getDouble("NFFC_Dynasty_PPR"), rs.getDouble("UNDERDOG_Dynasty_PPR"),
                    rs.getDouble("Sleeper_Dynasty_2QB"), rs.getDouble("ESPN_Dynasty_2QB"),
                    rs.getDouble("FANTRAX_Dynasty_2QB"), rs.getDouble("NFFC_Dynasty_2QB"),
                    rs.getDouble("UNDERDOG_Dynasty_2QB")
            );
            AggregateStat stats = new AggregateStat(
                    new SingleStat(rs.getDouble("CompletionPercentage_PROJECTED_AMOUNT"), rs.getDouble("CompletionPercentage_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("Passing2Pt_PROJECTED_AMOUNT"), rs.getDouble("Passing2Pt_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("PassAttempt_PROJECTED_AMOUNT"), rs.getDouble("PassAttempt_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("PassCompletion_PROJECTED_AMOUNT"), rs.getDouble("PassCompletion_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("PassingFirstDown_PROJECTED_AMOUNT"), rs.getDouble("PassingFirstDown_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("PassingInterception_PROJECTED_AMOUNT"), rs.getDouble("PassingInterception_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("PassingTd_PROJECTED_AMOUNT"), rs.getDouble("PassingTd_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("PassingYard_PROJECTED_AMOUNT"), rs.getDouble("PassingYard_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("Fumble_PROJECTED_AMOUNT"), rs.getDouble("Fumble_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("Receiving2Pt_PROJECTED_AMOUNT"), rs.getDouble("Receiving2Pt_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("Reception_PROJECTED_AMOUNT"), rs.getDouble("Reception_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("Reception40Plus_PROJECTED_AMOUNT"), rs.getDouble("Reception40Plus_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("ReceptionFirstDown_PROJECTED_AMOUNT"), rs.getDouble("ReceptionFirstDown_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("ReceivingTd_PROJECTED_AMOUNT"), rs.getDouble("ReceivingTd_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("ReceivingYard_PROJECTED_AMOUNT"), rs.getDouble("ReceivingYard_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("Rushing2Pt_PROJECTED_AMOUNT"), rs.getDouble("Rushing2Pt_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("RushingAttempt_PROJECTED_AMOUNT"), rs.getDouble("RushingAttempt_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("RushingFirstDown_PROJECTED_AMOUNT"), rs.getDouble("RushingFirstDown_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("RushingTd_PROJECTED_AMOUNT"), rs.getDouble("RushingTd_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("RushingYard_PROJECTED_AMOUNT"), rs.getDouble("RushingYard_PROJECTED_POINTS")),
                    new SingleStat(rs.getDouble("TeReceptionBonus_PROJECTED_AMOUNT"), rs.getDouble("TeReceptionBonus_PROJECTED_POINTS"))
            );
            String position = rs.getString("Position");
            return new Player(
                    rs.getLong("id"),
                    rs.getString("NormalizedName"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getInt("Age"),
                    rs.getInt("PositionalDepth"),
                    rs.getString("Notes"),
                    rs.getBoolean("IsSleeper"),
                    rs.getDouble("ECR"),
                    position,
                    rs.getString("TeamName"),
                    rs.getString("TeamAbbreviation"),
                    rs.getInt("ByeWeek"),
                    rs.getInt("StrengthOfSchedule"),
                    rs.getInt("Overall_Tier"),
                    rs.getInt(String.format("%s_Tier",position)),
                    adp,
                    stats
            );
        }
    }

}
