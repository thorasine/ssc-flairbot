package ssc_flairbot.league;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.builders.summoner.SummonerBuilder;
import no.stelar7.api.l4j8.impl.builders.thirdparty.ThirdPartyCodeBuilder;
import no.stelar7.api.l4j8.pojo.league.LeagueEntry;
import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.SecretFile;
import ssc_flairbot.persistence.User;

@Component
public class LeagueApi {

    @Autowired
    private RankHandler rankHandler;
    private L4J8 api;
    private List<String> availableRegions;

    @PostConstruct
    private void init() {
        this.api = new L4J8(SecretFile.CREDS);
        availableRegions = List.of("NA", "EUW", "EUNE", "BR", "LAN", "LAS", "JP", "KR", "OCE", "RU", "TR");
    }

    public Summoner getSummoner(User user) {
        if(!availableRegions.contains(user.getServer())){
            return null;
        }
        Summoner summoner  = new SummonerBuilder().withPlatform(platformConvert(user.getServer())).withName(user.getSummonerName()).get();
        if (summoner == null) {
            Logger.getLogger(LeagueApi.class.getName()).log(Level.INFO, "Summoner not found for: " + "/u/" + user.getRedditName() + " Summoner: " + user.getSummonerName() + "(" + user.getServer() + ")");
            return null;
        }
        return summoner;
    }

    public String getThirdPartyCode(User user) {
        String code = new ThirdPartyCodeBuilder().withPlatform(platformConvert(user.getServer())).withSummonerId(user.getSummonerId()).getCode();
        if (code == null) {
            //Logger.getLogger(LeagueApi.class.getName()).log(Level.INFO, "Verification code not found for: " + "/u/" + user.getRedditName() + " Summoner: " + user.getSummonerName() + "(" + user.getServer() + ")");
        }
        return code;
    }

    public String getHighestRank(User user) {
        List<LeagueEntry> leaguePositions = api.getLeagueAPI().getLeagueEntries(platformConvert(user.getServer()),user.getSummonerId());
        if (leaguePositions.isEmpty()) {
            return "Unranked";
        }
        return rankHandler.getSummonerHighestRank(leaguePositions);
    }

    private Platform platformConvert(String server) {
        switch (server) {
            case "NA":
                return Platform.NA1;
            case "EUW":
                return Platform.EUW1;
            case "EUNE":
                return Platform.EUN1;
            case "BR":
                return Platform.BR1;
            case "LAN":
                return Platform.LA1;
            case "LAS":
                return Platform.LA2;
            case "JP":
                return Platform.JP1;
            case "KR":
                return Platform.KR;
            case "OCE":
                return Platform.OC1;
            case "RU":
                return Platform.RU;
            case "TR":
                return Platform.TR1;
            default:
                return Platform.EUW1;
        }
    }
}
