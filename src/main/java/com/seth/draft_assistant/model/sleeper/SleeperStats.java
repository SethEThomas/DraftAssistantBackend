package com.seth.draft_assistant.model.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SleeperStats {
    @JsonProperty("rush_yd")
    private double rushYd;

    @JsonProperty("rush_td")
    private double rushTd;

    @JsonProperty("rush_fd")
    private double rushFd;

    @JsonProperty("rush_att")
    private double rushAtt;

    @JsonProperty("rush_2pt")
    private double rush2pt;

    @JsonProperty("rec_yd")
    private double recYd;

    @JsonProperty("rec_td")
    private double recTd;

    @JsonProperty("rec_fd")
    private double recFd;

    @JsonProperty("rec_5_9")
    private double rec5_9;

    @JsonProperty("rec_40p")
    private double rec40p;

    @JsonProperty("rec_30_39")
    private double rec30_39;

    @JsonProperty("rec_20_29")
    private double rec20_29;

    @JsonProperty("rec_10_19")
    private double rec10_19;

    @JsonProperty("rec_0_4")
    private double rec0_4;

    private double rec;

    @JsonProperty("pts_std")
    private double ptsStd;

    @JsonProperty("pts_ppr")
    private double ptsPpr;

    @JsonProperty("pts_half_ppr")
    private double ptsHalfPpr;

    private double gp;

    @JsonProperty("fum_lost")
    private double fumLost;

    @JsonProperty("bonus_rec_rb")
    private double bonusRecRb;

    @JsonProperty("adp_std")
    private double adpStd;

    @JsonProperty("adp_rookie")
    private double adpRookie;

    @JsonProperty("adp_ppr")
    private double adpPpr;

    @JsonProperty("adp_idp")
    private double adpIdp;

    @JsonProperty("adp_half_ppr")
    private double adpHalfPpr;

    @JsonProperty("adp_dynasty_std")
    private double adpDynastyStd;

    @JsonProperty("adp_dynasty_ppr")
    private double adpDynastyPpr;

    @JsonProperty("adp_dynasty_half_ppr")
    private double adpDynastyHalfPpr;

    @JsonProperty("adp_dynasty_2qb")
    private double adpDynasty2qb;

    @JsonProperty("adp_dynasty")
    private double adpDynasty;

    @JsonProperty("adp_2qb")
    private double adp2qb;
}

