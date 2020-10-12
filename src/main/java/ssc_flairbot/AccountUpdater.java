package ssc_flairbot;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ssc_flairbot.league.LeagueServer;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;

import javax.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handles updating the rank, database and flairs for the users.
 *
 * @author Thorasine
 */
@Component
public class AccountUpdater {

    private final DBHandler db;
    private final RankUpdateTask rankUpdateTask;
    private double threshold;
    private List<LeagueServer> servers;

    @Autowired
    public AccountUpdater(DBHandler db, RankUpdateTask rankUpdateTask) {
        this.db = db;
        this.rankUpdateTask = rankUpdateTask;
    }

    /**
     * Fill the servers array with the available Riot regions and their limiters.
     * <p>
     * threshold: Percentage of the total capacity the app is allowed to use for requests towards Riot's API.
     * <p>
     * The app and method limiters restrict the amount of request the app will send towards riot's API server. App and
     * method rate limiting are enforced per region.
     * Generally L4J8 handles rate limiting, but this case I want to control how much reasources (requests) the update
     * task can hug during updating the whole database, so the site will be able to send different requests towards
     * Riot's API so the user experience remains unaffected.
     */
    @PostConstruct
    private void init() {
        threshold = 0.8;
        int appLimit1 = 100;
        int appTimespan1 = 120_000;
        int appLimit2 = 20;
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
            server.methodLimit = (int) (server.methodLimit * threshold);
            server.methodLimiter = new RateLimiter(server.methodLimit, methodTimespan);
            server.appLimiter1 = new RateLimiter((int) (appLimit1 * threshold), appTimespan1);
            server.applimiter2 = new RateLimiter((int) (appLimit2 * threshold), appTimespan2);
        });
    }

    /**
     * Initiate an UpdateTask for every server on a new thread.
     *
     * @return "ok" string for testing purposes
     */
    String scheduledUpdate() {
        Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Scheduled database update.");
        int nThreads = servers.size();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        CompletableFuture[] futures = new CompletableFuture[nThreads];
        for (int i = 0; i < nThreads; i++) {
            Runnable runner = new UpdateTask(servers.get(i));
            futures[i] = CompletableFuture.runAsync(runner, executor);
        }
        CompletableFuture.allOf(futures).join();
        Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Finished: Scheduled database update.");
        return "ok";
    }

    /**
     * Helper class to update the database and flairs for given servers, 100 user at a time.
     */
    private class UpdateTask implements Runnable {

        private LeagueServer server;

        UpdateTask(LeagueServer server) {
            this.server = server;
        }

        /**
         * Updates the database and flair for given server 100 user at a time.
         */
        public void run() {
            List<User> accounts = db.getValidatedAccountsByServer(server.name);
            if (accounts.size() == 0) return;
            Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
            List<List<User>> lists = Lists.partition(accounts, 100);
            lists.forEach(chunk -> rankUpdateTask.update(chunk, server.methodLimiter, server.appLimiter1, server.applimiter2));
            Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Finished: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
        }
    }

}
