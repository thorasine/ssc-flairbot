package ssc_flairbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import ssc_flairbot.reddit.RedditApi;

/**
 * Class that handles task scheduling.
 *
 * @author Thorasine
 */
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

    /**
     * Fires every 5 minutes.
     * Check if the pending users have set their third party code already to verify their in-game accounts.
     */
    @Scheduled(cron = "0 */5 * * * *")
    private void updateVerifications() {
        verificationUpdater.scheduledUpdate();
    }

    /**
     * Fires every 55 minutes.
     * Update the reddit token that expires every hour.
     */
    @Scheduled(cron = "0 */55 * * * *")
    private void updateRedditToken() {
        redditApi.refreshToken();
    }

    /**
     * Fires every 6 hours.
     * Update the ranks, database and flairs for every account with fresh data.
     */
    @Scheduled(cron = "0 0 */6 * * *")
    private void updateAccounts() {
        accountUpdater.scheduledUpdate();
    }
}
