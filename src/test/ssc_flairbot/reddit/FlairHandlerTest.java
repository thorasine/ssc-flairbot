package ssc_flairbot.reddit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.league.LeagueRankHelper;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import java.util.ArrayList;
import java.util.List;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FlairHandler.class, RedditApi.class, DBHandler.class, LeagueRankHelper.class, TokenMaker.class})
public class FlairHandlerTest {

    @Autowired
    private FlairHandler flairHandler;
    @Autowired
    private DBHandler database;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private List<User> users = new ArrayList<>();

    @Before
    public void setUp() {
        database.dropTable();
        database.createTable();
        user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort").gamerank("Iron II").validated("validated").buildUser();
        user2 = new UserBuilder().redditName("Thorasine").summonerName("Thorasine").gamerank("Grandmaster I").validated("validated").buildUser();
        user3 = new UserBuilder().redditName("Its_Vizicsacsi").summonerName("Vizicsacsi").gamerank("Gold II").validated("validated").buildUser();
        user4 = new UserBuilder().redditName("Its_Vizicsacsi").summonerName("Betteraccount").gamerank("Diamond III").validated("pending").buildUser();
        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);
        database.addUser(user4);
        List<User> allUsers = database.getAllUsers();
        user1 = allUsers.get(0);
        user2 = allUsers.get(1);
        user3 = allUsers.get(2);
        user4 = allUsers.get(3);
    }

    //todo Write methods to retrive user's flair to be able to properly test these
    @Test
    public void onlyCompareValidatedAccounts() {
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        flairHandler.updateFlairs(users);
    }

    @Test
    public void properlyPartitioned(){
        user1.setGamerank("Challenger I");
        user3.setGamerank("Platinum IV");
        database.updateUser(user1);
        database.updateUser(user3);
        for(int i = 0; i < 100; i++){
            users.add(user1);
        }
        users.add(user3);
        flairHandler.updateFlairs(users);
    }
}