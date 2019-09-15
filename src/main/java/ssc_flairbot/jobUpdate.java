package ssc_flairbot;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.league.RankUpdater;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairHandler;
import ssc_flairbot.reddit.RateLimiter;

import javax.annotation.PostConstruct;
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

    private double      limit;
    private RateLimiter globalLimiter;
    private RateLimiter euwLimiter;
    private RateLimiter naLimiter;
    private RateLimiter euneLimiter;

    @PostConstruct
    private void init() {
        limit           =   0.5;
        globalLimiter   =   new RateLimiter((int) (500 * limit), 10_000);
        euwLimiter      =   new RateLimiter((int) (300 * limit), 60_000);
        euneLimiter     =   new RateLimiter((int) (165 * limit), 60_000);
        naLimiter       =   new RateLimiter((int) (270 * limit), 60_000);
    }

    //@Scheduled(cron = "0 */5 * * * *")
    public void scheduledUpdate() {
        Logger.getLogger(jobUpdate.class.getName()).log(Level.INFO, "Started: Updating database.");
        taskExecutor.execute(new UpdateTask("EUW", euwLimiter, globalLimiter));
        taskExecutor.execute(new UpdateTask("EUNE", euneLimiter, globalLimiter));
        taskExecutor.execute(new UpdateTask("NA", naLimiter, globalLimiter));
    }

    private class UpdateTask implements Runnable {

        private String      server;
        private RateLimiter limiter;
        private RateLimiter globalLimiter;

        public UpdateTask(String server, RateLimiter serverLimiter, RateLimiter globalLimiter) {
            this.server         = server;
            this.limiter        = serverLimiter;
            this.globalLimiter  = globalLimiter;
        }

        public void run() {
            List<User> accounts = db.getValidatedAccountsByServer(server);
            if(accounts.size() == 0) return;
            Logger.getLogger(jobUpdate.class.getName()).log(Level.INFO, "Started: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
            List<List<User>> lists = Lists.partition(accounts, 100);
            for (List<User> chunk : lists) {
                rankUpdater.update(chunk, limiter, globalLimiter);
            }
            Logger.getLogger(jobUpdate.class.getName()).log(Level.INFO, "Finished: Updating database and flairs for " + accounts.size() + " (" + server + ") users.");
        }

    }

}
