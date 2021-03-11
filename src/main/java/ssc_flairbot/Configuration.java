package ssc_flairbot;

import org.springframework.beans.factory.annotation.Value;

/**
 * Class to hold global configuration values.
 *
 * @author Thorasine
 */
public class Configuration {

    // wipes the database, puts in sample data, and changes League API rate limits to match a development key's values
    @Value("${custom.dev-mode}")
    public static boolean IN_DEVELOPMENT_PHASE;

    // percentage of the total capacity allowed to use for requests towards Riot's API for the periodic updating of
    // database
    public static final double UPDATE_THRESHOLD = 0.8;

    // the amount of times the app tries to validate an user before setting them to "failed"
    public static final int VERIFICATION_TRIES = 20;
}
