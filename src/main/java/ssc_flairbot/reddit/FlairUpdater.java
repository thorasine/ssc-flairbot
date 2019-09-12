package ssc_flairbot.reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FlairUpdater {

    @Autowired
    private TokenMaker tokenMaker;
    @Autowired
    private RedditApi api;

    private List<User> flairUpdatePool;

    @PostConstruct
    private void init() {
        refreshToken();
        flairUpdatePool = new ArrayList<>();
    }

    //Every 55 minutes
    @Scheduled(cron = "0 */55 * * * *")
    private void refreshToken() {
        api.updateToken(tokenMaker.getToken());
    }

    public void updateFlairs(List<User> users){
        if(users.isEmpty()) return;
        Logger.getLogger(FlairUpdater.class.getName()).log(Level.INFO, "Updating flairs started for " + users.size() + " users.");
        api.updateFlairs(users);
    }

}
