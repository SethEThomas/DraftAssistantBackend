package com.seth.draft_assistant.repository;

import com.seth.draft_assistant.model.enums.AdpType;
import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.enums.Position;
import com.seth.draft_assistant.model.enums.TypeTable;
import com.seth.draft_assistant.model.espn.EspnPlayer;
import com.seth.draft_assistant.model.internal.adp.AggregateAdp;
import com.seth.draft_assistant.model.internal.adp.InternalAdp;
import com.seth.draft_assistant.model.internal.player.Player;
import com.seth.draft_assistant.model.internal.requests.PlayerUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.RankUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.ScoringSettingUpdateRequest;
import com.seth.draft_assistant.model.internal.requests.TierUpdateRequest;
import com.seth.draft_assistant.model.internal.scoring.AggregateStat;
import com.seth.draft_assistant.model.internal.scoring.ProjectedStat;
import com.seth.draft_assistant.model.internal.scoring.ScoringSetting;
import com.seth.draft_assistant.model.internal.scoring.SingleStat;
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
public class ScoreRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_SCORE_TYPES_SQL = "SELECT * FROM SCORE_TYPE";
    private static final String SCORING_UPDATE_SQL = "INSERT INTO SCORE_TYPE (Id, PointValue) VALUES (?, ?) ON DUPLICATE KEY UPDATE PointValue = VALUES(PointValue)";

    public List<ScoringSetting> getScoringSettings(){
        return jdbcTemplate.query(SELECT_ALL_SCORE_TYPES_SQL, new ScoreSettingRowMapper());
    }

    public void updateScoringSettings(List<ScoringSettingUpdateRequest> request){
        jdbcTemplate.batchUpdate(SCORING_UPDATE_SQL, request, request.size(), (ps, argument) -> {
            ps.setInt(1, argument.getScoringSettingId());
            ps.setDouble(2, argument.getPointValue());
        });
    }

    private static class ScoreSettingRowMapper implements RowMapper<ScoringSetting>{
        @Override
        public ScoringSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ScoringSetting(
                    rs.getInt("id"),
                    rs.getString("Name"),
                    rs.getString("DisplayName"),
                    rs.getDouble("PointValue")
            );
        }
    }

}
