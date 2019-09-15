package ssc_flairbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.reddit.FlairHandler;

@Component
public class jobUpdate {
    @Autowired
    DBHandler db;
    @Autowired
    LeagueApi lolApi;
    @Autowired
    FlairHandler flairHandler;

    //@Scheduled(cron = "0 */5 * * * *")
    //@Scheduled(cron = "*/5 * * * * *")
    private void scheduledUpdate(){
        System.out.println("TEST MESSAGE PLEASE IGNORE");
    }
}
