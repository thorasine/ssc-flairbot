package ssc_flairbot.league;

import ssc_flairbot.RateLimiter;

/**
 * Class to hold the Riot API related server information.
 *
 * @author Thorasine
 */
public class LeagueServer {
    public String name;
    public int methodLimit;
    public RateLimiter appLimiter1;
    public RateLimiter applimiter2;
    public RateLimiter methodLimiter;

    public LeagueServer(String name, int methodLimit) {
        this.name = name;
        this.methodLimit = methodLimit;
    }
}
