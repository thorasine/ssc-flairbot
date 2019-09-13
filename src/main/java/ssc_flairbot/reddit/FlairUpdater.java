package ssc_flairbot.reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FlairUpdater {

    @Autowired
    private RedditApi api;

    public void updateFlairs(List<User> users){
        if(users.isEmpty()) return;
        Logger.getLogger(FlairUpdater.class.getName()).log(Level.INFO, "Updating flairs started for " + users.size() + " users.");
        api.updateFlairs(users);
    }

    public void test(){
        List<User> users = new ArrayList<>();
        users.add(new UserBuilder().redditName("Thorasine").rank("Platinum II").buildUser());
        users.add(new UserBuilder().redditName("Its_Vizicsacsi").rank("Gold II").buildUser());
        //try multithread execution
        for(int i = 0; i < 65; i++){
            api.updateFlairs(users);
        }
    }

}
