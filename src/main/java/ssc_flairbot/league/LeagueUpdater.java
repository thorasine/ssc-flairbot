package ssc_flairbot.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

@Component
public class LeagueUpdater {
    
    @Autowired
    LeagueApi lolApi;
    
    public void rankUpdate(User user){
        System.out.println(lolApi.getHighestRank(user));
    }
}
