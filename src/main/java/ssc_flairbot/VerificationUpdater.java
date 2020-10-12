package ssc_flairbot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairHandler;

/**
 * Class to check and update if the pendig users have set their third party code already to verify their in-game accounts.
 *
 * @author Thorasine
 */
@Component
public class VerificationUpdater {

    private final LeagueApi lolApi;
    private final DBHandler database;
    private final FlairHandler flairHandler;

    private static final int TRIES_UNTIL_FAIL = 10;

    @Autowired
    public VerificationUpdater(LeagueApi lolApi, DBHandler database, FlairHandler flairHandler) {
        this.lolApi = lolApi;
        this.database = database;
        this.flairHandler = flairHandler;
    }

    /**
     * Get the pending users, request and compare their validation code and update the user according to it. If a user have
     * passed the check, update their reddit flair.
     */
    void scheduledUpdate() {
        List<User> users = database.getPendingUsers();
        List<User> verifiedUsers = new ArrayList<>();
        Logger.getLogger(VerificationUpdater.class.getName()).log(Level.INFO, "Started: Updating verification session for " + users.size() + " users.");
        for (User user : users) {
            checkValidationCode(user);
            database.updateUser(user);
            if (user.getValidated().equalsIgnoreCase("validated")) {
                verifiedUsers.add(user);
            }
        }
        Logger.getLogger(VerificationUpdater.class.getName()).log(Level.INFO, "Ended: Updating verifications have been successfully completed.");
        if (!verifiedUsers.isEmpty()) {
            flairHandler.updateFlairs(verifiedUsers);
        }
    }

    /**
     * Check through Riot API if a given user have set their third party code to the same as the app required them to.
     *
     * @param user the user whose code we check
     */
    private void checkValidationCode(User user) {
        String apiCode = lolApi.getThirdPartyCode(user);
        if (apiCode == null) {
            validationFailed(user);
            return;
        }
        if (!apiCode.equalsIgnoreCase(user.getValidationCode())) {
            validationFailed(user);
            return;
        }
        validationSuccess(user);
    }

    /**
     * Increases the times the app tried to validate the user counter by one. If it has passed the TRIES_UNTIL_FAIL counter
     * the user's validated status is set to "failed".
     *
     * @param user the user who failed the third party code check
     */
    private void validationFailed(User user) {
        user.setValidationTries(user.getValidationTries() + 1);
        if (user.getValidationTries() > TRIES_UNTIL_FAIL) {
            user.setValidated("failed");
        }
    }

    /**
     * Set the user's validated status to "validated". It is now a legitimate in-game account of the user.
     *
     * @param user the user who passed the third party code check
     */
    private void validationSuccess(User user) {
        user.setValidationTries(user.getValidationTries() + 1);
        user.setValidated("validated");
    }
}
