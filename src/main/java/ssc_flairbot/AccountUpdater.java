package ssc_flairbot;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class AccountUpdater {

    private final DBHandler db;
    private final RankUpdateTask rankUpdateTask;
    private double threshold;
    private RateLimiter globalLimiter;
    private List<String> servers;
    private List<RateLimiter> limiters;

    @Autowired
    public AccountUpdater(DBHandler db, RankUpdateTask rankUpdateTask) {
        this.db = db;
        this.rankUpdateTask = rankUpdateTask;
    }

    @PostConstruct
    private void init() {
        threshold = 0.5;
        int globalLimit = 500;
        int globalTimespan = 10_000;
        int serverTimespan = 60_000;
        servers = Arrays.asList("EUW", "NA", "EUNE", "BR", "KR", "LAN", "LAS", "TR", "OCE", "JP", "RU");
        List<Integer> serverLimits = Arrays.asList(300, 270, 165, 90, 90, 80, 80, 60, 55, 35, 35);
        globalLimiter = new RateLimiter((int) (globalLimit * threshold), globalTimespan);
        serverLimits.forEach(serverLimit -> serverLimit = (int) (serverLimit * threshold));
        limiters = new ArrayList<>();
        for (Integer serverLimit : serverLimits) {
            limiters.add(new RateLimiter(serverLimit, serverTimespan));
        }
    }

    String scheduledUpdate() {
        Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Scheduled database update.");
        int nThreads = servers.size();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        CompletableFuture[] futures = new CompletableFuture[nThreads];
        for (int i = 0; i < nThreads; i++) {
            Runnable runner = new UpdateTask(servers.get(i), limiters.get(i), globalLimiter);
            futures[i] = CompletableFuture.runAsync(runner, executor);
        }
        CompletableFuture.allOf(futures).join();
        Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Finished: Scheduled database update.");
        return "ok";
    }

    private class UpdateTask implements Runnable {

        private String server;
        private RateLimiter limiter;
        private RateLimiter globalLimiter;

        UpdateTask(String server, RateLimiter serverLimiter, RateLimiter globalLimiter) {
            this.server = server;
            this.limiter = serverLimiter;
            this.globalLimiter = globalLimiter;
        }

        public void run() {
            List<User> accounts = db.getValidatedAccountsByServer(server);
            if (accounts.size() == 0) return;
            Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
            List<List<User>> lists = Lists.partition(accounts, 100);
            lists.forEach(chunk -> rankUpdateTask.update(chunk, limiter, globalLimiter));
            Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Finished: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
        }
    }

}
