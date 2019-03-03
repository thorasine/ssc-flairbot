package ssc_flairbot.league;

import java.util.List;
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

    //Every 5 minutes
    @Scheduled(cron="0 */5 * * * *")
    public void update() {
        List<User> users = db.getPendingUsers();
        for (User user : users) {
            String apiCode = lolApi.getThirdPartyCode(user);
            if (apiCode != null) {
                if (apiCode.equalsIgnoreCase(user.getValidationCode())) {
                    user.setValidated("validated");
                } else {
                    user.setValidationTries(user.getValidationTries() + 1);
                    if (user.getValidationTries() > 10) {
                        user.setValidated("failed");
                    }
                }
            } else {
                user.setValidationTries(user.getValidationTries() + 1);
                if (user.getValidationTries() > 10) {
                    user.setValidated("failed");
                }
            }
            db.updateVerification(user);
        }
    }

}
