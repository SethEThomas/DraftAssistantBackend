package com.seth.draft_assistant;

public class Constants {
    public static final String CURRENT_YEAR = "2024";
    public static final String SLEEPER_ADP_URL_TEMPLATE = "https://api.sleeper.com/projections/nfl/%s?season_type=regular&position[]=QB&position[]=RB&position[]=TE&position[]=WR&order_by=adp_ppr";
    public static final String ESPN_ADP_URL_TEMPLATE = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/%s/segments/0/leaguedefaults/3?scoringPeriodId=0&view=kona_player_info";
    public static final String ESPN_FANTASY_FILTER_HEADER = "x-fantasy-filter";
    public static final String ESPN_FANTASY_FILTER_VALUE = "{\"players\": {\"limit\": 1500,\"sortDraftRanks\":{\"sortPriority\": 100,\"sortAsc\": true,\"value\": \"STANDARD\"}}}";
    public static final String ROTOWIRE_URL = "https://www.rotowire.com/football/tables/adp.php?pos=ALL";
}
