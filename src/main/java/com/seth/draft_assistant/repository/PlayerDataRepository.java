package com.seth.draft_assistant.repository;

import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.enums.Team;
import com.seth.draft_assistant.model.internal.InternalAdp;
import com.seth.draft_assistant.model.sleeper.SleeperProjection;
import com.seth.draft_assistant.model.sleeper.SleeperStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PlayerDataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void savePlayerData(List<SleeperProjection> playerData) {
        for (SleeperProjection player : playerData) {
            Long playerId = insertPlayer(player);
            if(playerId != null){
                insertAdp(playerId, player.getStats().getAdp());
                insertProjectedStats(playerId, player.getStats());
            }
        }
    }

    private Long insertPlayer(SleeperProjection playerData) {
        String sql = "INSERT INTO PLAYER(Position, FirstName, LastName, Team) VALUES (?, ?, ?, ?)";
        String firstName = playerData.getPlayer().getFirstName();
        String lastName = playerData.getPlayer().getLastName();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, getPositionId(playerData.getPlayer().getPosition()));
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.setInt(4, getTeamId(playerData.getTeam()));
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

    private void insertAdp(long playerId, List<InternalAdp> adps){
        for(InternalAdp adp: adps){
            String sql = String.format("INSERT INTO ADP(`PlayerId`,`ADPType`,`%s`) VALUES(?,?,?)",getAdpColumnName(adp.getDataSource()));
            try {
                jdbcTemplate.update(sql,
                        playerId,
                        adp.getAdpType().getId(),
                        adp.getAdp());
            } catch (Exception e) {
                System.out.println(String.format("Unable to insert player data for player %d", playerId));
                e.printStackTrace();
            }
        }
    }

    private void insertProjectedStats(long playerId, SleeperStats stats){

    }

    private String getAdpColumnName(DataSource dataSource){
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
}