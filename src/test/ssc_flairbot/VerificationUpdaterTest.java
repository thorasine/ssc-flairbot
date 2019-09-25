package ssc_flairbot;

import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@TestPropertySource(properties = "app.scheduling.enable=false")
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest
public class VerificationUpdaterTest {

    @Autowired
    VerificationUpdater verificationUpdater;
    @Autowired
    DBHandler database;

    private static boolean setUpIsDone = false;
    private final L4J8 riotApi = new L4J8(SecretFile.CREDS);
    private static List<User> users = new ArrayList<>();

    @Before
    public void setUp() {
        if (setUpIsDone) {
            database.dropTable();
            database.createTable();
            database.addUser(users.get(0));
            return;
        }
        final List<SpectatorParticipant> participants = new SpectatorBuilder().withPlatform(Platform.EUW1).getFeaturedGames().get(0).getParticipants();
        for (SpectatorParticipant p : participants) {
            Summoner summoner = new SummonerBuilder().withPlatform(Platform.EUW1).withName(p.getSummonerName()).get();
            User user = new UserBuilder().redditName(summoner.getName()).summonerName(summoner.getName())
                    .summonerId(summoner.getSummonerId()).server("EUW").validated("pending").validationTries(0)
                    .validationCode("QWERTY").buildUser();
            users.add(user);
        }
        setUpIsDone = true;
        setUp();
    }

    @Test
    public void validationSessionCompleted() {
        verificationUpdater.scheduledUpdate();
        assertEquals("Validation tries is not 1.", database.getAllUsers().get(0).getValidationTries(), 1);
        assertEquals("Validation tries is not pending.", database.getAllUsers().get(0).getValidated(), "pending");
    }

    @Test
    public void userSetToFailedAfterManyTries() {
        User user = database.getAllUsers().get(0);
        user.setValidationTries(10);
        database.updateUser(user);
        verificationUpdater.scheduledUpdate();
        assertEquals("User didn't change from pending to failed after 11 tries.", database.getAllUsers().get(0).getValidated(), "failed");
    }

    @Test
    public void updatedMultipleUsersSuccessfully() {
        database.addUser(users.get(1));
        database.addUser(users.get(2));
        List<User> dbUsers = database.getAllUsers();
        User user2 = dbUsers.get(1);
        user2.setValidationTries(10);
        database.updateUser(user2);
        User user3 = dbUsers.get(2);
        user3.setValidated("validated");
        int user3Tries = user3.getValidationTries();
        database.updateUser(user3);

        verificationUpdater.scheduledUpdate();
        assertEquals("Validation tries is not 1 for user1.", database.getAllUsers().get(0).getValidationTries(), 1);
        assertEquals("Validation tries is not pending. for user1", database.getAllUsers().get(0).getValidated(),"pending");
        assertEquals("Validation tries is not 11 for user2.", database.getUserById(user2.getId()).getValidationTries(), 11);
        assertEquals("Validation tries is not failed for user2.", database.getUserById(user2.getId()).getValidated(), "failed");
        assertEquals("User3 got updated despite being validated.", database.getUserById(user3.getId()).getValidationTries(), user3Tries);
        assertEquals("Validation tries is not validated for user3", database.getUserById(user3.getId()).getValidated(), "validated");
    }

}