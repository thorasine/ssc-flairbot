package ssc_flairbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to create sample data for manual testing
 *
 * @author Thorasine
 */
@Component
public class ManualTest {

    private final Logger logger = Logger.getLogger(ManualTest.class.getName());
    private final DBHandler database;

    @Autowired
    public ManualTest(DBHandler database) {
        this.database = database;
    }

    @PostConstruct
    private void startTest() {
        if (Configuration.IN_DEVELOPMENT_PHASE) {
            logger.log(Level.WARNING, "--DEVELOPMENT PHASE ENABLED!--");
            logger.log(Level.WARNING, "--This will wipe the database and set the League API rate limits to much lower!--");
            putInSampleData();
        }
    }

    /**
     * Reset the database and put in sample data for testing purposes.
     */
    private void putInSampleData() {
        database.dropTable();
        database.createTable();

        User user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("8kFIUtL2QHnAKmyI485jY7bWifUk6poPC1KQehEbjtr6zCc").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERFK").validationTries(0).buildUser();

        User user2 = new UserBuilder().redditName("Thorasine").summonerName("Oreena")
                .summonerId("YKuxOWZtcpkiE1V07iLisX1s93FdVSEf3TnHEYHBqy0Qe-8").server("EUW").rank("Gold IV").validated("pending")
                .validationCode("83ITES").validationTries(0).buildUser();

        User user3 = new UserBuilder().redditName("Thorasine").summonerName("SecretSmurf")
                .summonerId("dDsBSlaEk34758KbRuwTnydTNaC1nZQZ5kGOwboGfbb-Zz4").server("NA").rank("Challenger I").validated("validated")
                .validationCode("TEST754").validationTries(0).buildUser();

        User user4 = new UserBuilder().redditName("Vjostar").summonerName("Vjostar")
                .summonerId("wWb-WB9G7sMRNTKCjnze7MX84wkIEJhmqe-2JsRwuu_WsEk").server("EUW").rank("Silver III").validated("pending")
                .validationCode("TEST434").validationTries(0).buildUser();

        User user5 = new UserBuilder().redditName("Holo").summonerName("Holó")
                .summonerId("_mwCqsqdyUplPbKpVdc9_k65LAmIm6RonekeZ36L0x5F1Ek").server("EUW").rank("Bronze II").validated("validated")
                .validationCode("TEST12").validationTries(0).buildUser();

        User user6 = new UserBuilder().redditName("Oprah").summonerName("Oprah Winfrey")
                .summonerId("O06SyEvi6NVHMbIuisc_iAKh1g82vAEk6jvzzca25pov8oE").server("EUW").rank("Bronze I").validated("validated")
                .validationCode("TEST239").validationTries(0).buildUser();

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);
        database.addUser(user4);
        database.addUser(user5);
        database.addUser(user6);
    }
}
