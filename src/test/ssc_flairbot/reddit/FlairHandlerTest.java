package ssc_flairbot.reddit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.league.RankHandler;
import ssc_flairbot.persistence.DBHandler;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FlairHandler.class, RedditApi.class, DBHandler.class, RankHandler.class, TokenMaker.class})
public class FlairHandlerTest {

    @Autowired
    private FlairHandler flairHandler;
    @Autowired
    private DBHandler database;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private List<User> users = new ArrayList<>();;

    @Before
    public void setUp() {
        database.dropTable();
        database.createTable();
        user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort").rank("Iron II").validated("validated").buildUser();
        user2 = new UserBuilder().redditName("Thorasine").summonerName("Thorasine").rank("Grandmaster I").validated("validated").buildUser();
        user3 = new UserBuilder().redditName("Its_Vizicsacsi").summonerName("Vizicsacsi").rank("Gold II").validated("validated").buildUser();
        user4 = new UserBuilder().redditName("Its_Vizicsacsi").summonerName("Betteraccount").rank("Diamond III").validated("pending").buildUser();
        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);
        database.addUser(user4);
        List<User> allUsers = database.getAllUsers();
        user1 = database.getUserById(allUsers.get(0).getId());
        user2 = database.getUserById(allUsers.get(1).getId());
        user3 = database.getUserById(allUsers.get(2).getId());
        user4 = database.getUserById(allUsers.get(3).getId());
    }

    //Can't really test these automatically without writing a method for retriving users flair from reddit
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
        user1.setRank("Challenger I");
        user3.setRank("Platinum IV");
        database.updateUser(user1);
        database.updateUser(user3);
        for(int i = 0; i < 100; i++){
            users.add(user1);
        }
        users.add(user3);
        flairHandler.updateFlairs(users);
    }
}