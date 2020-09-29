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

@Component
public class VerificationUpdater {

    private final LeagueApi lolApi;
    private final DBHandler database;
    private final FlairHandler flairHandler;

    private final int TRIES_UNTIL_FAIL = 10;

    @Autowired
    public VerificationUpdater(LeagueApi lolApi, DBHandler database, FlairHandler flairHandler) {
        this.lolApi = lolApi;
        this.database = database;
        this.flairHandler = flairHandler;
    }

    public void scheduledUpdate() {
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

    private void validationFailed(User user) {
        user.setValidationTries(user.getValidationTries() + 1);
        if (user.getValidationTries() > TRIES_UNTIL_FAIL) {
            user.setValidated("failed");
        }
    }

    private void validationSuccess(User user) {
        user.setValidationTries(user.getValidationTries() + 1);
        user.setValidated("validated");
    }
}
