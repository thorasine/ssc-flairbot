package ssc_flairbot;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.league.RankUpdater;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairHandler;
import ssc_flairbot.reddit.RateLimiter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class jobUpdate {
    @Autowired
    DBHandler db;
    @Autowired
    LeagueApi lolApi;
    @Autowired
    FlairHandler flairHandler;
    @Autowired
    RankUpdater rankUpdater;

    @Qualifier("applicationTaskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

    private double threshold;
    private RateLimiter globalLimiter;
    private List<String> servers;
    private List<RateLimiter> limiters;

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

    @Scheduled(cron = "0 0 */6 * * *")
    public void scheduledUpdate() {
        Logger.getLogger(jobUpdate.class.getName()).log(Level.INFO, "Started: Updating database.");
        for(int i = 0; i < servers.size(); i++){
            taskExecutor.execute(new UpdateTask(servers.get(i), limiters.get(i), globalLimiter));
        }
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
            Logger.getLogger(jobUpdate.class.getName()).log(Level.INFO, "Started: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
            List<List<User>> lists = Lists.partition(accounts, 100);
            for (List<User> chunk : lists) {
                rankUpdater.update(chunk, limiter, globalLimiter);
            }
            Logger.getLogger(jobUpdate.class.getName()).log(Level.INFO, "Finished: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
        }

    }

}
