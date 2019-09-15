package ssc_flairbot.reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.RankHandler;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FlairHandler {

    @Autowired
    private RedditApi redditApi;
    @Autowired
    private DBHandler database;
    @Autowired
    private RankHandler rankHandler;

    public void updateFlairs(List<User> users){
        if (users.isEmpty()) return;
        //Logger.getLogger(FlairHandler.class.getName()).log(Level.INFO, "Started: Updating flairs for " + users.size() + " users.");
        List<List<User> > lists = Lists.partition(users, 100);
        for(List<User> chunk : lists) {
            Map<String, String> flairMap = new HashMap<>();
            for (User user : chunk) {
                List<User> accounts = new ArrayList<>();
                Set<String> ranks = new HashSet<>();
                accounts = database.getValidatedAccountsByRedditName(user.getRedditName());
                for (User account : accounts) {
                    ranks.add(account.getRank());
                }
                String rank = rankHandler.getHighestRank(ranks);
                if(!rank.equalsIgnoreCase("Unranked")){
                    flairMap.put(user.getRedditName(), rank);
                }
            }
            redditApi.updateRankedFlairs(flairMap);
        }
        //Logger.getLogger(FlairHandler.class.getName()).log(Level.INFO, "Finished: Updating flairs for " + users.size() + " users.");
    }

    public void updateFlairsOld(List<User> users) {
        if (users.isEmpty()) return;
        Logger.getLogger(FlairHandler.class.getName()).log(Level.INFO, "Started: Updating flairs for " + users.size() + " users.");
        List<List<User> > lists = Lists.partition(users, 100);
        for(List<User> chunk : lists){
            redditApi.updateFlairs(chunk);
        }
    }

    public void test() {
        redditApi.testMethod();
        redditApi.testMethod();

    }

    public void test2(){
        List<User> users = new ArrayList<>();
        User user1 = new UserBuilder().redditName("Thorasine").rank("Iron IV").buildUser();
        User user2 = new UserBuilder().redditName("Its_Vizicsacsi").rank("Silver IV").validated("validated").buildUser();
        database.addUser(user2);
        for(int i = 0; i < 2001; i++){
            users.add(user1);
        }
        users.add(user2);
        updateFlairs(users);
    }

}
