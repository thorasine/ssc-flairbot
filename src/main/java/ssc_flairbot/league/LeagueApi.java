package ssc_flairbot.league;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.LeaguePositions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

@Component
public class LeagueApi {

    @Autowired
    private RankHandler rankHandler;

    @PostConstruct
    private void init() {
        Orianna.setRiotAPIKey("RGAPI-b0997214-f26d-4aea-9414-d67f995d5466");
    }

    public Summoner getSummoner(User user) {
        Summoner summoner = Summoner.named(user.getSummonerName()).withRegion(regionConvert(user.getServer())).get();
        if (!summoner.exists()) {
            Logger.getLogger(LeagueApi.class.getName()).log(Level.INFO, "Summoner not found for: " + "/u/" + user.getRedditName() + " Summoner: " + user.getSummonerName() + "(" + user.getServer() + ")");
            return null;
        }
        return summoner;
    }

    public String getThirdPartyCode(User user) {
        Summoner summoner = Summoner.withId(user.getSummonerId()).withRegion(regionConvert(user.getServer())).get();
        String code = summoner.getVerificationString().getString();
        if (code == null) {
            //Logger.getLogger(LeagueApi.class.getName()).log(Level.INFO, "Verification code not found for: " + "/u/" + user.getRedditName() + " Summoner: " + user.getSummonerName() + "(" + user.getServer() + ")");
        }
        return code;
    }

    public String getHighestRank(User user) {
        Summoner summoner;
        summoner = Summoner.withId(user.getSummonerId()).withRegion(regionConvert(user.getServer())).get();
        LeaguePositions leaguePositions = summoner.getLeaguePositions();
        if (!leaguePositions.exists()) {
            return "Unranked";
        }
        return rankHandler.getSummonerHighestRank(leaguePositions);
    }

    private Region regionConvert(String server) {
        switch (server) {
            case "NA":
                return Region.NORTH_AMERICA;
            case "EUW":
                return Region.EUROPE_WEST;
            case "EUNE":
                return Region.EUROPE_NORTH_EAST;
            case "BR":
                return Region.BRAZIL;
            case "LAN":
                return Region.LATIN_AMERICA_NORTH;
            case "LAS":
                return Region.LATIN_AMERICA_SOUTH;
            case "JP":
                return Region.JAPAN;
            case "KR":
                return Region.KOREA;
            case "OCE":
                return Region.OCEANIA;
            case "RU":
                return Region.RUSSIA;
            case "TR":
                return Region.TURKEY;
            default:
                return Region.EUROPE_WEST;
        }
    }
}
