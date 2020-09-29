package ssc_flairbot;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
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
    private final TaskExecutor taskExecutor;

    private double threshold;
    private RateLimiter globalLimiter;
    private List<String> servers;
    private List<RateLimiter> limiters;

    @Autowired
    public AccountUpdater(DBHandler db, RankUpdateTask rankUpdateTask, @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.db = db;
        this.rankUpdateTask = rankUpdateTask;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    private void init() {
        threshold = 0.5;
        globalLimiter = new RateLimiter((int) (500 * threshold), 10_000);
        servers = Arrays.asList("EUW", "NA", "EUNE", "BR", "KR", "LAN", "LAS", "TR", "OCE", "JP", "RU");
        List<Integer> serverLimits = Arrays.asList(300, 270, 165, 90, 90, 80, 80, 60, 55, 35, 35);
        serverLimits.forEach(item -> item = (int) (item * threshold));
        limiters = new ArrayList<>();
        for (Integer serverLimit : serverLimits) {
            limiters.add(new RateLimiter(serverLimit, 60_000));
        }
    }

    public void scheduledUpdateOld() {
        Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Updating database.");
        for (int i = 0; i < servers.size(); i++) {
            taskExecutor.execute(new UpdateTask(servers.get(i), limiters.get(i), globalLimiter));
        }
    }

    public String scheduledUpdate() {
        Logger.getLogger(AccountUpdater.class.getName()).log(Level.INFO, "Started: Updating database.");
        ExecutorService executor = Executors.newFixedThreadPool(11);
        CompletableFuture[] futures = new CompletableFuture[11];
        for (int i = 0; i < servers.size(); i++) {
            Runnable runner = new UpdateTask(servers.get(i), limiters.get(i), globalLimiter);
            futures[i] = CompletableFuture.runAsync(runner, executor);
        }
        CompletableFuture.allOf(futures).join();
        return "ok";
    }

    private class UpdateTask implements Runnable {

        private String server;
        private RateLimiter limiter;
        private RateLimiter globalLimiter;

        public UpdateTask(String server, RateLimiter serverLimiter, RateLimiter globalLimiter) {
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
