package ssc_flairbot;

import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.builders.spectator.SpectatorBuilder;
import no.stelar7.api.l4j8.impl.builders.summoner.SummonerBuilder;
import no.stelar7.api.l4j8.pojo.spectator.SpectatorParticipant;
import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@TestPropertySource(properties = "app.scheduling.enable=false")
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest
public class VerificationUpdaterTest {

    @Autowired
    VerificationUpdater verificationUpdater;
    @Autowired
    DBHandler db;

    private int verificationTries = VerificationUpdater.verificationTries;
    private boolean setupNeeded = true;
    private List<User> users = new ArrayList<>();

    @Before
    public void setUp() {
        if (setupNeeded) {
            final List<SpectatorParticipant> participants = new SpectatorBuilder().withPlatform(Platform.EUW1).getFeaturedGames().get(0).getParticipants();
            for (SpectatorParticipant p : participants) {
                Summoner summoner = new SummonerBuilder().withPlatform(Platform.EUW1).withName(p.getSummonerName()).get();
                User user = new UserBuilder().redditName(summoner.getName()).summonerName(summoner.getName())
                        .summonerId(summoner.getSummonerId()).server("EUW").validated("pending").validationTries(0)
                        .validationCode("QWERTY").buildUser();
                users.add(user);
            }
            setupNeeded = false;
        }
        db.dropTable();
        db.createTable();
        db.addUser(users.get(0));
    }

    @Test
    public void validationSessionCompleted() {
        verificationUpdater.scheduledUpdate();
        assertThat(db.getAllUsers().get(0).getValidationTries()).isEqualTo(1);
        assertThat(db.getAllUsers().get(0).getValidated()).isEqualTo("pending");
    }

    @Test
    public void userSetToFailedAfterManyTries() {
        User user = db.getAllUsers().get(0);
        user.setValidationTries(verificationTries);
        db.updateUser(user);
        verificationUpdater.scheduledUpdate();
        assertThat(db.getAllUsers().get(0).getValidated()).isEqualTo("failed");
    }

    @Test
    public void updatedMultipleUsersSuccessfully() {
        db.addUser(users.get(1));
        db.addUser(users.get(2));
        List<User> dbUsers = db.getAllUsers();
        User user2 = dbUsers.get(1);
        user2.setValidationTries(verificationTries);
        db.updateUser(user2);
        User user3 = dbUsers.get(2);
        user3.setValidated("validated");
        int user3Tries = user3.getValidationTries();
        db.updateUser(user3);

        verificationUpdater.scheduledUpdate();
        assertThat(db.getAllUsers().get(0).getValidationTries()).isEqualTo(1);
        assertThat(db.getAllUsers().get(0).getValidated()).isEqualTo("pending");
        assertThat(db.getUserById(user2.getId()).getValidationTries()).isEqualTo(verificationTries + 1);
        assertThat(db.getUserById(user2.getId()).getValidated()).isEqualTo("failed");
        assertThat(db.getUserById(user3.getId()).getValidationTries()).isEqualTo(user3Tries);
        assertThat(db.getUserById(user3.getId()).getValidated()).isEqualTo("validated");
    }

}