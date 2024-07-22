package com.seth.draft_assistant.model.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seth.draft_assistant.model.enums.AdpType;
import com.seth.draft_assistant.model.enums.DataSource;
import com.seth.draft_assistant.model.internal.InternalAdp;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Data
public class SleeperStats {
    @JsonProperty("rush_yd")
    private double rushYd = 0.0;

    @JsonProperty("rush_td")
    private double rushTd = 0.0;

    @JsonProperty("rush_fd")
    private double rushFd = 0.0;

    @JsonProperty("rush_att")
    private double rushAtt = 0.0;

    @JsonProperty("rush_2pt")
    private double rush2pt = 0.0;

    @JsonProperty("rec_yd")
    private double recYd = 0.0;

    @JsonProperty("rec_td")
    private double recTd = 0.0;

    @JsonProperty("rec_fd")
    private double recFd = 0.0;

    @JsonProperty("rec_5_9")
    private double rec5_9 = 0.0;

    @JsonProperty("rec_40p")
    private double rec40p = 0.0;

    @JsonProperty("rec_30_39")
    private double rec30_39 = 0.0;

    @JsonProperty("rec_20_29")
    private double rec20_29 = 0.0;

    @JsonProperty("rec_10_19")
    private double rec10_19 = 0.0;

    @JsonProperty("rec_0_4")
    private double rec0_4 = 0.0;

    @JsonProperty("rec")
    private double rec = 0.0;

    @JsonProperty("pts_std")
    private double ptsStd = 0.0;

    @JsonProperty("pts_ppr")
    private double ptsPpr = 0.0;

    @JsonProperty("pts_half_ppr")
    private double ptsHalfPpr = 0.0;

    @JsonProperty("gp")
    private double gp = 0.0;

    @JsonProperty("fum_lost")
    private double fumLost = 0.0;

    @JsonProperty("bonus_rec_rb")
    private double bonusRecRb = 0.0;

    @JsonProperty("adp_std")
    private double adpStd = 0.0;

    @JsonProperty("adp_rookie")
    private double adpRookie = 0.0;

    @JsonProperty("adp_ppr")
    private double adpPpr = 0.0;

    @JsonProperty("adp_idp")
    private double adpIdp = 0.0;

    @JsonProperty("adp_half_ppr")
    private double adpHalfPpr = 0.0;

    @JsonProperty("adp_dynasty_std")
    private double adpDynastyStd = 0.0;

    @JsonProperty("adp_dynasty_ppr")
    private double adpDynastyPpr = 0.0;

    @JsonProperty("adp_dynasty_half_ppr")
    private double adpDynastyHalfPpr = 0.0;

    @JsonProperty("adp_dynasty_2qb")
    private double adpDynasty2qb = 0.0;

    @JsonProperty("adp_dynasty")
    private double adpDynasty = 0.0;

    @JsonProperty("adp_2qb")
    private double adp2qb = 0.0;

    @JsonProperty("pass_yd")
    private double passYd = 0.0;

    @JsonProperty("pass_td")
    private double passTd = 0.0;

    @JsonProperty("pass_int")
    private double passInt = 0.0;

    @JsonProperty("pass_fd")
    private double passFd = 0.0;

    @JsonProperty("pass_cmp")
    private double passCmp = 0.0;

    @JsonProperty("pass_att")
    private double passAtt = 0.0;

    @JsonProperty("pass_2pt")
    private double pass2pt = 0.0;

    @JsonProperty("cmp_pct")
    private double cmpPct = 0.0;

    public List<InternalAdp> getAdp(){
        List<InternalAdp> adps = new ArrayList<>();
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.STANDARD, adpStd));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.HALF_PPR, adpHalfPpr));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.PPR, adpPpr));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.TWO_QB, adp2qb));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.DYNASTY_STANDARD, adpDynastyStd));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.DYNASTY_HALF_PPR, adpDynastyHalfPpr));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.DYNASTY_PPR, adpDynastyPpr));
        adps.add(new InternalAdp(DataSource.SLEEPER, AdpType.DYNASTY_2_QB, adpDynasty2qb));
        return adps;
    }
}
