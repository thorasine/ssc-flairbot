package ssc_flairbot.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

@Component
public class LeagueApi {

    private final ApiConfig config = new ApiConfig().setKey("RGAPI-890449c1-983c-40a5-b6ca-581605c56084");
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
    
    public String checkThirdPartyCode(User user){
        Platform enumServer = Platform.valueOf(user.getServer());
        String apiCode = "";
        try {
            apiCode = api.getThirdPartyCodeBySummoner(enumServer, user.getSummonerId());
        } catch (RiotApiException ex) {
            Logger.getLogger(LeagueApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(apiCode.equalsIgnoreCase(user.getValidationCode())){
            return "ok";
        }
        return "";
    }
    
    public void setHighestRank(User user){
        
    }
}
