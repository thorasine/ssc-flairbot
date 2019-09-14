package ssc_flairbot.league;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.reddit.FlairUpdater;

@Component
public class VerificationUpdater {

    @Autowired
    LeagueApi lolApi;
    @Autowired
    DBHandler db;
    @Autowired
    FlairUpdater flairUpdater;

    private final int tries = 10;

    //Every 5 minutes
    //@Scheduled(cron = "0 */5 * * * *")
    //@Scheduled(cron = "30 * * * * *")
    public void update() {
        List<User> users = db.getPendingUsers();
        List<User> verifiedUsers = new ArrayList<>();
        Logger.getLogger(VerificationUpdater.class.getName()).log(Level.INFO, "Updating verification session started for " + users.size() + " users.");
        for (User user : users) {
            checkValidationCode(user);
            db.updateUser(user);
            if (user.getValidated().equalsIgnoreCase("validated")) {
                verifiedUsers.add(user);
            }
        }
        Logger.getLogger(VerificationUpdater.class.getName()).log(Level.INFO, "Updating verifications have been successfully completed.");
        if (!verifiedUsers.isEmpty()) {
            flairUpdater.updateFlairs(verifiedUsers);
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
        if (user.getValidationTries() > tries) {
            user.setValidated("failed");
        }
    }

    private void validationSuccess(User user) {
        user.setValidationTries(user.getValidationTries() + 1);
        user.setValidated("validated");
    }
}
