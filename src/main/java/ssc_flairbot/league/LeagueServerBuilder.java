package ssc_flairbot.league;

import ssc_flairbot.Configuration;
import ssc_flairbot.RateLimiter;

import java.util.Arrays;
import java.util.List;

/**
 * Class to build the Riot API related server information holders with given threshold.
 *
 * @author Thorasine
 */
public class LeagueServerBuilder {

    private double threshold;
    private List<LeagueServer> servers;

    /**
     * Return a list of LeagueServer configured towards an Riot API method
     *
     * @param threshold percentage of the total capacity is allowed to use for requests towards Riot's API for the
     *                  updating the database
     * @return a list of configured LeagueServer
     */
    public List<LeagueServer> getServers(double threshold) {
        if (this.threshold != threshold || servers.size() == 0) {
            this.threshold = threshold;
            createLeagueServers();
        }
        return servers;
    }

    /**
     * Fill servers with LeagueServer with available region name, method limiter and two app limiters all adjusted
     * to the threshold.
     */
    private void createLeagueServers() {
        int appLimit1;
        int appLimit2;

        if (Configuration.IN_DEVELOPMENT_PHASE) {
            appLimit1 = 100;
            appLimit2 = 20;
        } else {
            appLimit1 = 30_000;
            appLimit2 = 500;
        }
        int appTimespan1 = 120_000;
        int appTimespan2 = 10_000;
        int methodTimespan = 60_000;
        servers = Arrays.asList(new LeagueServer("EUW", 2000),
                new LeagueServer("NA", 2000),
                new LeagueServer("EUNE", 1600),
                new LeagueServer("BR", 1300),
                new LeagueServer("KR", 200),
                new LeagueServer("LAN", 1000),
                new LeagueServer("LAS", 1000),
                new LeagueServer("TR", 1300),
                new LeagueServer("OCE", 800),
                new LeagueServer("JP", 800),
                new LeagueServer("RU", 600));

        servers.forEach(server -> {
            server.methodLimiter = new RateLimiter((int) (server.methodLimit * threshold), methodTimespan);
            server.appLimiter1 = new RateLimiter((int) (appLimit1 * threshold), appTimespan1);
            server.appLimiter2 = new RateLimiter((int) (appLimit2 * threshold), appTimespan2);
            server.addLimiters();
        });
    }
}
