package ssc_flairbot.webcontrollers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.league.RankHelper;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;
import ssc_flairbot.reddit.TokenMaker;

import static org.junit.Assert.*;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Logic.class, LeagueApi.class, RankHelper.class, DBHandler.class, TokenMaker.class})
public class LogicTest {

    @Autowired
    Logic logic;
    @Autowired
    LeagueApi lolApi;
    @Autowired
    DBHandler database;

    private User user;
    private User user2;

    @Before
    public void setUp() {
        database.dropTable();
        database.createTable();
        user = new UserBuilder().redditName("Thorasine").summonerName("Trefort").server("EUW").buildUser();
        user2 = new UserBuilder().redditName("Its_Vizicsacsi").summonerName("Vizicsacsi").server("EUW").validated("validated").buildUser();
        database.addUser(user2);
    }

    @Test
    public void addUser() {
        assertEquals("Failed to add Thorasine:Trefort.", "ok", logic.addUser(user));
    }

    @Test
    public void addUserWithoutSummonerName() {
        user.setSummonerName("");
        assertNotEquals("Added user without summonername.", "ok", logic.addUser(user));
    }

    @Test
    public void addNonExistingSummoner() {
        user.setSummonerName("dsguheriu32zr3q23dnn353");
        assertNotEquals("Non-existent summoner added.", "ok", logic.addUser(user));
    }

    @Test
    public void addAlreadyRegisteredSummoner() {
        logic.addUser(user);
        assertNotEquals("Already registered summoner by User added.", "ok", logic.addUser(user));
    }

    @Test
    public void addAlreadyValidatedSummoner() {
        user.setSummonerName("Vizicsacsi");
        assertNotEquals("Already validated summoner (Vizicsacsi) added", "ok", logic.addUser(user));
    }

    @Test
    public void deleteUser() {
        User user = database.getAllUsers().get(0);
        assertEquals("Couldn't delete user.", "ok", logic.deleteUser(user.getRedditName(), user.getId()));
        assertEquals("Account didn't get deleted.", 0, database.getAllUsers().size());
    }

    @Test
    public void deleteNonExistentUser() {
        user.setRedditName("Its_Vizicsacsi");
        assertNotEquals("Non-existent account got deleted.", "ok", logic.deleteUser(user.getRedditName(), 999L));
    }

    @Test
    public void deletedNotOwnedUser() {
        Long id = database.getAllUsers().get(0).getId();
        assertNotEquals("Deleted not owned user.", "ok", logic.deleteUser(user.getRedditName(), id));
    }
}