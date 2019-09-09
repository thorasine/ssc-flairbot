package ssc_flairbot.league;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;

@Component
public class VerificationUpdater {

    @Autowired
    LeagueApi lolApi;
    @Autowired
    DBHandler db;

    private final int tries = 10;

    //Every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void update() {
        List<User> users = db.getPendingUsers();
        Logger.getLogger(VerificationUpdater.class.getName()).log(Level.INFO,"Updating session started for " + users.size() + " users.");
        for (User user : users) {
            checkValidationCode(user);
            db.updateUser(user);
        }
        Logger.getLogger(VerificationUpdater.class.getName()).log(Level.INFO, "Updating session finished");
    }

    private void checkValidationCode(User user) {
        //To ensure no 2 people can validate the same account at the same time
        if(user.getValidated().equalsIgnoreCase("validated")){
            user.setValidationTries(100);
            validationFailed(user);
            return;
        }

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
