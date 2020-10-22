package ssc_flairbot.league;

import ssc_flairbot.RateLimiter;

/**
 * Class to hold the Riot API method related server information.
 *
 * @author Thorasine
 */
public class LeagueServer {

    int methodLimit;
    private String name;
    RateLimiter appLimiter1;
    RateLimiter applimiter2;
    RateLimiter methodLimiter;

    LeagueServer(String name, int methodLimit) {
        this.name = name;
        this.methodLimit = methodLimit;
    }

    public String getName() {
        return name;
    }

    public RateLimiter getAppLimiter1() {
        return appLimiter1;
    }

    public RateLimiter getApplimiter2() {
        return applimiter2;
    }

    public RateLimiter getMethodLimiter() {
        return methodLimiter;
    }
}
