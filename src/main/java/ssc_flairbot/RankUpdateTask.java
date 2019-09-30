package ssc_flairbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairHandler;

import java.util.List;

@Component
public class RankUpdateTask {

    @Autowired
    LeagueApi lolApi;
    @Autowired
    DBHandler database;
    @Autowired
    FlairHandler flairHandler;

    public void update(List<User> users, RateLimiter limiter, RateLimiter globalLimiter) {
        for (User user : users) {
            globalLimiter.acquire();
            globalLimiter.enter();
            limiter.acquire();
            limiter.enter();
            user.setRank(lolApi.getHighestRank(user));
        }
        database.batchUpdateUsersRank(users);
        flairHandler.updateFlairs(users);
    }
}
