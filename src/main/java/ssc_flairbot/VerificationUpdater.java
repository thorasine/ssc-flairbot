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
 * Class to check and update if the pending users have set their third party code already to verify their in-game
 * accounts.
 *
 * @author Thorasine
 */
@Component
public class VerificationUpdater {

    private final LeagueApi lolApi;
    private final DBHandler database;
    private final FlairHandler flairHandler;
    private final Logger logger = Logger.getLogger(VerificationUpdater.class.getName());
    static final int verificationTries = Configuration.getVerificationTries();

    @Autowired
    public VerificationUpdater(LeagueApi lolApi, DBHandler database, FlairHandler flairHandler) {
        this.lolApi = lolApi;
        this.database = database;
        this.flairHandler = flairHandler;
    }

    /**
     * Get the pending users, request and compare their validation code and update the user according to it. If a
     * user have passed the check, update their reddit flair.
     */
    void scheduledUpdate() {
        List<User> users = database.getPendingUsers();
        List<User> verifiedUsers = new ArrayList<>();
        logger.log(Level.INFO, "Started: Updating verification session for " + users.size() + " users.");
        for (User user : users) {
            checkValidationCode(user);
            database.updateUser(user);
            if (user.getValidated().equalsIgnoreCase("validated")) {
                verifiedUsers.add(user);
            }
        }
        logger.log(Level.INFO, "Ended: Updating verifications have been successfully completed.");
        if (!verifiedUsers.isEmpty()) {
            flairHandler.updateFlairs(verifiedUsers);
        }
    }

    /**
     * Check through Riot API if a given user have set their third party code to the same as the app required them to.
     * Set the user's status accordingly if the validation was successful or failed more than the allowed amount.
     *
     * @param user the user whose code we check
     */
    private void checkValidationCode(User user) {
        String apiCode = lolApi.getThirdPartyCode(user);
        user.setValidationTries(user.getValidationTries() + 1);
        if (apiCode == null || !apiCode.equalsIgnoreCase(user.getValidationCode())) {
            if (user.getValidationTries() > verificationTries) {
                user.setValidated("failed");
            }
        } else {
            user.setValidated("validated");
        }
    }
}
