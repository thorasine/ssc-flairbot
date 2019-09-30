package ssc_flairbot.reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.RankHandler;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;

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

    public void updateFlairs(List<User> users) {
        if (users.isEmpty()) return;
        Logger.getLogger(FlairHandler.class.getName()).log(Level.FINE, "Started: Updating flairs for " + users.size() + " users.");
        List<List<User>> lists = Lists.partition(users, 100);
        for (List<User> chunk : lists) {
            Map<String, String> flairMap = new HashMap<>();
            chunk.forEach(user -> {
                String rank = getRedditHighestRank(user);
                if (rank != null) {
                    flairMap.put(user.getRedditName(), rank);
                }
            });
            redditApi.updateRankedFlairs(flairMap);
        }
        Logger.getLogger(FlairHandler.class.getName()).log(Level.FINE, "Finished: Updating flairs for " + users.size() + " users.");
    }

    private String getRedditHighestRank(User user) {
        Set<String> ranks = new HashSet<>();
        List<User> accounts = database.getValidatedAccountsByRedditName(user.getRedditName());
        for (User account : accounts) {
            ranks.add(account.getRank());
        }
        String rank = rankHandler.getHighestRank(ranks);
        if (!rank.equalsIgnoreCase("Unranked")) {
            return rank;
        }
        return null;
    }
}
