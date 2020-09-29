package ssc_flairbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import ssc_flairbot.reddit.RedditApi;

@ConditionalOnProperty(value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true)
@Configuration
public class ScheduledTasks {

    private final VerificationUpdater verificationUpdater;
    private final AccountUpdater accountUpdater;
    private final RedditApi redditApi;

    @Autowired
    public ScheduledTasks(VerificationUpdater verificationUpdater, AccountUpdater accountUpdater, RedditApi redditApi) {
        this.verificationUpdater = verificationUpdater;
        this.accountUpdater = accountUpdater;
        this.redditApi = redditApi;
    }

    @Scheduled(cron = "0 */5 * * * *")
    private void updateVerifications() {
        verificationUpdater.scheduledUpdate();
    }

    @Scheduled(cron = "0 0 */6 * * *")
    private void updateAccounts() {
        accountUpdater.scheduledUpdate();
    }

    @Scheduled(cron = "0 */55 * * * *")
    private void updateRedditToken() {
        redditApi.refreshToken();
    }
}
