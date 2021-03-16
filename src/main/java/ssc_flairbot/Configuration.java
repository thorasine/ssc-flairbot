package ssc_flairbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class to hold global configuration values.
 *
 * @author Thorasine
 */
@Component
public class Configuration {

    // wipes the database, puts in sample data, and changes League API rate limits to match a development key's values
    private static boolean inDevelopmentPhase;

    // percentage of the total capacity allowed to use for requests towards Riot's API for the periodic updating of
    // database
    private static double updateThreshold = 0.8;

    // the amount of times the app tries to validate an user before setting them to "failed"
    private static int verificationTries = 20;

    @Value("${custom.developmentMode}")
    private void setInDevelopmentPhase(Boolean devPhase) {
        System.out.println("SETTING DEV PHASE");
        inDevelopmentPhase = devPhase;
    }

    @Value("${custom.updateThreshold}")
    private void setUpdateThreshold(double threshold) {
        System.out.println("SETTING UPDATE THERSHOLD TO: " + threshold);
        updateThreshold = threshold;
    }

    @Value("${custom.verificationTries}")
    private void setVerificationTries(int verTries) {
        verificationTries = verTries;
    }

    public static boolean isInDevelopmentPhase() {
        return inDevelopmentPhase;
    }

    public static double getUpdateThreshold() {
        return updateThreshold;
    }

    public static int getVerificationTries() {
        return verificationTries;
    }
}
