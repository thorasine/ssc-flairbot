package ssc_flairbot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;
import ssc_flairbot.reddit.FlairHandler;

import static org.junit.Assert.*;

@TestPropertySource(properties = "app.scheduling.enable=false")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class VerificationUpdaterTest {

    @Autowired
    VerificationUpdater verificationUpdater;
    @Autowired
    LeagueApi lolApi;
    @Autowired
    DBHandler database;

    @Before
    public void setUp() {
        database.dropTable();
        database.createTable();

        User user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("8kFIUtL2QHnAKmyI485jY7bWifUk6poPC1KQehEbjtr6zCc").server("EUW").validated("validated")
                .validationCode("83ERFK").validationTries(0).buildUser();

        User user2 = new UserBuilder().redditName("Thorasine").summonerName("Oreena")
                .summonerId("YKuxOWZtcpkiE1V07iLisX1s93FdVSEf3TnHEYHBqy0Qe-8").server("EUW").validated("pending")
                .validationCode("ASFK732").validationTries(0).buildUser();

        User user3 = new UserBuilder().redditName("Thorasine").summonerName("SecretSmurf")
                .summonerId("dDsBSlaEk34758KbRuwTnydTNaC1nZQZ5kGOwboGfbb-Zz4").server("NA").validated("validated")
                .validationCode("TEST754").validationTries(0).buildUser();

        User user4 = new UserBuilder().redditName("Vjostar").summonerName("Vjostar")
                .summonerId("wWb-WB9G7sMRNTKCjnze7MX84wkIEJhmqe-2JsRwuu_WsEk").server("EUW").validated("pending")
                .validationCode("QWERTY").validationTries(0).buildUser();

        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);
        database.addUser(user4);
    }

    @Test
    public void update() {
        verificationUpdater.scheduledUpdate();
    }
}