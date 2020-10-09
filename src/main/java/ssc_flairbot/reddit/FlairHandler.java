package ssc_flairbot.reddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.RankHelper;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manages the handling (setting and updating) of user flairs on reddit through reddit's API.
 *
 * @author Thorasine
 */
@Component
public class FlairHandler {

    private final RedditApi redditApi;
    private final DBHandler database;
    private final RankHelper rankHelper;

    @Autowired
    public FlairHandler(RedditApi redditApi, DBHandler database, RankHelper rankHelper) {
        this.redditApi = redditApi;
        this.database = database;
        this.rankHelper = rankHelper;
    }

    /**
     * Update the reddit flairs for the given users throught reddit's API. Breaks down the requests into 100 users
     * chunks, because that is the maximum this API method can handle.
     *
     * @param users the users whose reddit flairs we want to update
     */
    public void updateFlairs(List<User> users) {
        if (users.isEmpty()) return;
        Logger.getLogger(FlairHandler.class.getName()).log(Level.FINE, "Started: Updating flairs for " + users.size() + " users.");
        int chunkSize = 100;
        List<List<User>> lists = Lists.partition(users, chunkSize);
        for (List<User> chunk : lists) {
            Map<String, String> flairMap = new HashMap<>();
            chunk.forEach(user -> {
                String rank = getAccountHighestRank(user);
                if (rank != null) {
                    flairMap.put(user.getRedditName(), rank);
                }
            });
            redditApi.setFlairs(flairMap);
        }
        Logger.getLogger(FlairHandler.class.getName()).log(Level.FINE, "Finished: Updating flairs for " + users.size() + " users.");
    }

    /**
     * Return the highest rank for a given user out of all the verified summoners attached to that reddit account from
     * the database.
     *
     * @param user whose highest rank is needed
     * @return the highest rank string
     */
    private String getAccountHighestRank(User user) {
        Set<String> ranks = new HashSet<>();
        List<User> accounts = database.getValidatedAccountsByRedditName(user.getRedditName());
        for (User account : accounts) {
            ranks.add(account.getRank());
        }
        String rank = rankHelper.getHighestRank(ranks);
        if (!rank.equalsIgnoreCase("Unranked")) {
            return rank;
        }
        return null;
    }
}
