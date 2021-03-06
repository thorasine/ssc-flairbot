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
     * @param users    the users we want to update
     * @param limiters the app and method limiters
     */
    void update(List<User> users, List<RateLimiter> limiters) {
        for (User user : users) {
            for (RateLimiter limiter : limiters) {
                limiter.acquire();
                limiter.enter();
            }
            user.setGamerank(lolApi.getRank(user));
        }
        database.batchUpdateUsersRank(users);
        flairHandler.updateFlairs(users);
    }
}
