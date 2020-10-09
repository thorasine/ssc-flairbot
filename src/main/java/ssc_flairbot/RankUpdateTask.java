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

    private final LeagueApi lolApi;
    private final DBHandler database;
    private final FlairHandler flairHandler;

    @Autowired
    public RankUpdateTask(LeagueApi lolApi, DBHandler database, FlairHandler flairHandler) {
        this.lolApi = lolApi;
        this.database = database;
        this.flairHandler = flairHandler;
    }

    public void update(List<User> users, RateLimiter limiter, RateLimiter globalLimiter) {
        for (User user : users) {
            globalLimiter.acquire();
            globalLimiter.enter();
            limiter.acquire();
            limiter.enter();
            user.setRank(lolApi.getRank(user));
        }
        database.batchUpdateUsersRank(users);
        flairHandler.setFlairs(users);
    }
}
