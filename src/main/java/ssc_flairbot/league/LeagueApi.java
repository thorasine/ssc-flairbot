package ssc_flairbot.league;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import no.stelar7.api.l4j8.basic.APICredentials;
import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.builders.summoner.SummonerBuilder;
import no.stelar7.api.l4j8.impl.builders.thirdparty.ThirdPartyCodeBuilder;
import no.stelar7.api.l4j8.pojo.league.LeagueEntry;
import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

/**
 * Class that handles the communication between the app and Riot's REST API with the help of L4J8 library.
 *
 * @author Thorasine
 */

@Component
public class LeagueApi {

    private final Logger logger = Logger.getLogger(LeagueApi.class.getName());
    private final LeagueRankHelper leagueRankHelper;
    private L4J8 api;
    private List<String> availableRegions;
    @Value("${riot.apiKey}")
    private String apiKey;

    @Autowired
    public LeagueApi(LeagueRankHelper leagueRankHelper) {
        this.leagueRankHelper = leagueRankHelper;
    }

    /**
     * Set the credentials for the L4J8 library and the available regions.
     */
    @PostConstruct
    void init() {
        APICredentials CREDS = new APICredentials(apiKey, "optional tournament key");
        this.api = new L4J8(CREDS);
        availableRegions = List.of("NA", "EUW", "EUNE", "BR", "LAN", "LAS", "JP", "KR", "OCE", "RU", "TR");
    }

    /**
     * Return the user's in-game information through Riot's API.
     *
     * @param user the user whose summonerName and server will identify their in-game profile
     * @return the user's in-game info (including their rank)
     */
    public Summoner getSummoner(User user) {
        if (!availableRegions.contains(user.getServer())) {
            logger.log(Level.INFO, "Server not found for: /u/" + user.getRedditName() +
                    " server: " + user.getServer());
            return null;
        }
        Summoner summoner = new SummonerBuilder()
                .withPlatform(platformConvert(user.getServer()))
                .withName(user.getSummonerName())
                .get();
        if (summoner == null) {
            logger.log(Level.INFO, "Summoner not found for:" +
                    " /u/" + user.getRedditName() +
                    " Summoner: " + user.getSummonerName() +
                    " (" + user.getServer() + ")");
            return null;
        }
        return summoner;
    }

    /**
     * Return the user's third party code (that they set) using Riot's API, to verify their identity.
     *
     * @param user the user whose code we wish to retrieve
     * @return the user's third party code
     */
    public String getThirdPartyCode(User user) {
        String code = new ThirdPartyCodeBuilder()
                .withPlatform(platformConvert(user.getServer()))
                .withSummonerId(user.getSummonerId())
                .getCode();
        if (code == null) {
            logger.log(Level.FINE, "Verification code not found for:" +
                    " /u/" + user.getRedditName() +
                    " Summoner: " + user.getSummonerName() +
                    " (" + user.getServer() + ")");
        }
        return code;
    }

    /**
     * Return the user's 5v5 in-game rank using Riot's API. If the user is not 5v5 ranked, then returns Unranked.
     *
     * @param user the user whose rank we wish to retrieve
     * @return the user's rank or Unranked
     */
    public String getRank(User user) {
        List<LeagueEntry> leaguePositions =
                api.getLeagueAPI().getLeagueEntries(platformConvert(user.getServer()), user.getSummonerId());
        if (leaguePositions.isEmpty()) {
            return "Unranked";
        }
        return leagueRankHelper.get5v5SoloRank(leaguePositions);
    }

    /**
     * Convert a String object into a corresponding Platform one. Platform is the required format for server
     * identification in the L4J8 library.
     *
     * @param server the server which we want to convert to
     * @return a Platform that we can use for Riot API request
     */
    public Platform platformConvert(String server) {
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
        }
        return null;
    }
}
