package ssc_flairbot.league;

import ssc_flairbot.RateLimiter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold the Riot API method related server information.
 *
 * @author Thorasine
 */
public class LeagueServer {

    int methodLimit;
    private String name;
    RateLimiter appLimiter1;
    RateLimiter appLimiter2;
    RateLimiter methodLimiter;
    private List<RateLimiter> limiters = new ArrayList<>();

    LeagueServer(String name, int methodLimit) {
        this.name = name;
        this.methodLimit = methodLimit;
    }

    void addLimiters() {
        if (limiters.size() == 0) {
            limiters.add(appLimiter1);
            limiters.add(appLimiter2);
            limiters.add(methodLimiter);
        }
    }

    public String getName() {
        return name;
    }

    public RateLimiter getAppLimiter1() {
        return appLimiter1;
    }

    public RateLimiter getAppLimiter2() {
        return appLimiter2;
    }

    public RateLimiter getMethodLimiter() {
        return methodLimiter;
    }

    public List<RateLimiter> getAllLimiters() {
        return limiters;
    }
}
