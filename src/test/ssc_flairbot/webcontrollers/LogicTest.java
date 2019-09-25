package ssc_flairbot.webcontrollers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.league.RankHandler;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;
import ssc_flairbot.reddit.TokenMaker;

import static org.junit.Assert.*;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Logic.class, LeagueApi.class, RankHandler.class, DBHandler.class, TokenMaker.class})
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
        assertEquals("Failed to add Thorasine:Trefort.", logic.addUser(user), "ok");
    }

    @Test
    public void addUserWithoutSummonerName() {
        user.setSummonerName("");
        assertNotEquals("Added user without summonername.", logic.addUser(user), "ok");
    }

    @Test
    public void addNonExistingSummoner() {
        user.setSummonerName("dsguheriu32zr3q23dnn353");
        assertNotEquals("Non-existent summoner added.", logic.addUser(user), "ok");
    }

    @Test
    public void addAlreadyRegisteredSummoner() {
        logic.addUser(user);
        assertNotEquals("Already registered summoner by User added.", logic.addUser(user), "ok");
    }

    @Test
    public void addAlreadyValidatedSummoner() {
        user.setSummonerName("Vizicsacsi");
        assertNotEquals("Already validated summoner (Vizicsacsi) added", logic.addUser(user), "ok");
    }

    @Test
    public void deleteUser() {
        User user = database.getAllUsers().get(0);
        assertEquals("Couldn't delete user.", logic.deleteUser(user.getRedditName(), user.getId()), "ok");
        assertEquals("Account didn't get deleted.", database.getAllUsers().size(), 0);
    }

    @Test
    public void deleteNonExistentUser() {
        user.setRedditName("Its_Vizicsacsi");
        assertNotEquals("Non-existent account got deleted.", logic.deleteUser(user.getRedditName(), 999L), "ok");
    }

    @Test
    public void deletedNotOwnedUser() {
        Long id = database.getAllUsers().get(0).getId();
        assertNotEquals("Deleted not owned user.", logic.deleteUser(user.getRedditName(), id), "ok");
    }
}