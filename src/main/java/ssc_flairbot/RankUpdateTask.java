package ssc_flairbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairHandler;

import java.util.List;

/**
 * Class that handles the updating of ranks for the given users.
 *
 * @author Thorasine
 */
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

    /**
     * Get the given users rank from in-game, update said users in the database and set their flairs on reddit to their
     * current in-game rank.
     *
     * @param users         the users we want to update
     * @param methodLimiter the method limiter
     * @param appLimiter1   the first app limiter
     * @param applimiter2   the second app limiter
     */
    void update(List<User> users, RateLimiter methodLimiter, RateLimiter appLimiter1, RateLimiter applimiter2) {
        for (User user : users) {
            appLimiter1.acquire();
            appLimiter1.enter();
            applimiter2.acquire();
            applimiter2.enter();
            methodLimiter.acquire();
            methodLimiter.enter();
            user.setRank(lolApi.getRank(user));
        }
        database.batchUpdateUsersRank(users);
        flairHandler.updateFlairs(users);
    }
}
