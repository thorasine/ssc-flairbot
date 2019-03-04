package ssc_flairbot.league;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

@Component
public class LeagueApi {

    @Autowired
    private RankHandler rankHandler;

    private final ApiConfig config = new ApiConfig().setKey("RGAPI-82be84ce-ce04-4be2-9487-e51e40920a41");
    private final RiotApi api = new RiotApi(config);

    public Summoner getSummoner(User user) {
        Platform enumServer;
        try {
            enumServer = Platform.valueOf(user.getServer());
        } catch (IllegalArgumentException ex) {
            System.out.println("USER'S (/u/" + user.getRedditName() + ")'S SERVER" + user.getServer() + " NOT FOUND");
            return null;
        }

        Summoner summoner = null;
        try {
            summoner = api.getSummonerByName(enumServer, user.getSummonerName());
        } catch (RiotApiException ex) {
            System.out.println("USER'S (/u/" + user.getRedditName() + ")'S SUMMONER " + user.getSummonerName() + " (" + user.getServer() + ") NOT FOUND");
        }
        return summoner;
    }

    public String getThirdPartyCode(User user) {
        Platform enumServer = Platform.valueOf(user.getServer());
        String apiCode = null;
        try {
            apiCode = api.getThirdPartyCodeBySummoner(enumServer, user.getSummonerId());
        } catch (RiotApiException ex) {
            if (ex.getErrorCode() != 404) {
                Logger.getLogger(LeagueApi.class.getName()).log(Level.SEVERE, "[Code: " + ex.getErrorCode() + " Msg: " + ex.getMessage() + "]");
            } else {
                //Logger.getLogger(LeagueApi.class.getName()).log(Level.WARNING, "[Code: " + ex.getErrorCode() + " Msg: " + ex.getMessage() + "]" + 
                //           " Failed verification, summoner has no code set: /u/" + user.getRedditName() + " SUMMONER: " + user.getSummonerName() + " (" + user.getServer() + ")");
            }
        }

        return apiCode;
    }

    public String getHighestRank(User user) {
        Set<LeaguePosition> positions;
        try {
            positions = api.getLeaguePositionsBySummonerId(Platform.valueOf(user.getServer()), user.getSummonerId());
        } catch (RiotApiException ex) {
            System.out.println("ERROR TRYING TO RETRIEVE SUMMONER'S RANK FOR: /u/" + user.getRedditName() + " SUMMONER: " + user.getSummonerName() + " (" + user.getServer() + ")");
            return null;
        }
        return rankHandler.getSummonerHighestRank(positions);
    }
}
