package ssc_flairbot.reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FlairUpdater {

    @Autowired
    private RedditApi api;

    public void updateFlairs(List<User> users) {
        if (users.isEmpty()) return;
        Logger.getLogger(FlairUpdater.class.getName()).log(Level.INFO, "Updating flairs started for " + users.size() + " users.");
        List<List<User> > lists = Lists.partition(users, 100);
        for(List<User> chunk : lists){
            api.updateFlairs(chunk);
        }
    }

    public void test() {
        api.regularUpdateFlairs();
        api.regularUpdateFlairs();

    }

    public void test2(){
        List<User> users = new ArrayList<>();
        User user1 = new UserBuilder().redditName("Thorasine").rank("Silver II").buildUser();
        User user2 = new UserBuilder().redditName("Its_Vizicsacsi").rank("Bronze III").buildUser();
        for(int i = 0; i < 101; i++){
            users.add(user1);
        }
        users.add(user2);
        updateFlairs(users);
    }

}
