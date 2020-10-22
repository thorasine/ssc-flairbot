package ssc_flairbot;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ssc_flairbot.league.LeagueServer;
import ssc_flairbot.league.LeagueServerBuilder;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;

import javax.annotation.PostConstruct;

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
    private List<LeagueServer> servers;

    @Autowired
    public AccountUpdater(DBHandler db, RankUpdateTask rankUpdateTask) {
        this.db = db;
        this.rankUpdateTask = rankUpdateTask;
    }

    /**
     * Create the server handlers with the given threshold
     */
    @PostConstruct
    private void init() {
        //percentage of the total capacity allowed to use for requests towards Riot's API for the updating the database
        double threshold = 0.8;
        LeagueServerBuilder serverBuilder = new LeagueServerBuilder();
        servers = serverBuilder.getServers(threshold);
    }

    /**
     * Initiate an UpdateTask for every server on a new thread.
     */
    void scheduledUpdate() {
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
    }

    /**
     * Helper class to update the database and flairs for given servers, 100 users at a time.
     */
    private class UpdateTask implements Runnable {

        private LeagueServer server;

        UpdateTask(LeagueServer server) {
            this.server = server;
        }

        /**
         * Update the database and flair for given server 100 users at a time.
         */
        public void run() {
            List<User> accounts = db.getValidatedAccountsByServer(server.getName());
            if (accounts.size() == 0){
                return;
            }
            Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
            List<List<User>> lists = Lists.partition(accounts, 100);
            lists.forEach(chunk -> rankUpdateTask.update(chunk, server.getMethodLimiter(), server.getAppLimiter1(), server.getApplimiter2()));
            Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Finished: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
        }
    }

}
