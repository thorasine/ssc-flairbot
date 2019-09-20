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
    private RedditApi redditApi;
    @Autowired
    private DBHandler database;


    @Before
    public void setUp() {
    }

    @Test
    public void updateFlairs() {

    }

    @Test
    public void properChunking(){
        List<User> users = new ArrayList<>();
        User user1 = new UserBuilder().redditName("Thorasine").rank("Iron IV").buildUser();
        User user2 = new UserBuilder().redditName("Its_Vizicsacsi").rank("Silver IV").validated("validated").buildUser();
        for(int i = 0; i < 2001; i++){
            users.add(user1);
        }
        users.add(user2);
        //updateFlairs(users);
    }
}