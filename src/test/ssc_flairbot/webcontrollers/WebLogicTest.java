package ssc_flairbot.webcontrollers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.league.LeagueApi;
import ssc_flairbot.league.LeagueRankHelper;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;
import ssc_flairbot.reddit.TokenMaker;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebLogic.class, LeagueApi.class, LeagueRankHelper.class, DBHandler.class, TokenMaker.class})
public class WebLogicTest {

    @Autowired
    WebLogic webLogic;
    @Autowired
    LeagueApi lolApi;
    @Autowired
    DBHandler db;

    private User user;
    private User user2;

    @Before
    public void setUp() {
        db.dropTable();
        db.createTable();
        user = new UserBuilder().redditName("Thorasine").summonerName("Trefort").server("EUW").buildUser();
        user2 = new UserBuilder().redditName("Its_Vizicsacsi").summonerName("Vizicsacsi").server("EUW").validated("validated").buildUser();
        db.addUser(user2);
    }

    @Test
    public void addUser() {
        assertThat(webLogic.addUser(user)).isEqualTo("ok");
    }

    @Test
    public void addUserWithoutSummonerName() {
        user.setSummonerName("");
        assertThat(webLogic.addUser(user)).isNotEqualTo("ok");
    }

    @Test
    public void addNonExistingSummoner() {
        user.setSummonerName("this summoner name cannot exist");
        assertThat(webLogic.addUser(user)).isNotEqualTo("ok");
    }

    @Test
    public void addAlreadyRegisteredSummoner() {
        webLogic.addUser(user);
        assertThat(webLogic.addUser(user)).isNotEqualTo("ok");
    }

    @Test
    public void addAlreadyValidatedSummoner() {
        user.setSummonerName("Vizicsacsi");
        assertThat(webLogic.addUser(user)).isNotEqualTo("ok");
    }

    @Test
    public void deleteUser() {
        User user = db.getAllUsers().get(0);
        assertThat(webLogic.deleteUser(user.getRedditName(), user.getId())).isEqualTo("ok");
        assertThat(db.getAllUsers().size()).isEqualTo(0);
    }

    @Test
    public void deleteNonExistentUser() {
        user.setRedditName("Its_Vizicsacsi");
        assertThat(webLogic.deleteUser(user.getRedditName(), 999L)).isNotEqualTo("ok");
    }

    @Test
    public void deletedNotOwnedUser() {
        Long id = db.getAllUsers().get(0).getId();
        assertThat(webLogic.deleteUser(user.getRedditName(), id)).isNotEqualTo("ok");
    }
}