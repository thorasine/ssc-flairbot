package ssc_flairbot.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@JdbcTest
@RunWith(SpringRunner.class)
@ComponentScan
public class DBHandlerTest {

    @Autowired
    private DBHandler database;

    private User user1;
    private User user2;

    @Before
    public void setUp() {
        user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("8kFIUtL2QHnAKmyI485jY7bWifUk6poPC1KQehEbjtr6zCc").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERFK").validationTries(0).buildUser();
        user2 = new UserBuilder().redditName("Thorasine").summonerName("Oreena")
                .summonerId("YKuxOWZtcpkiE1V07iLisX1s93FdVSEf3TnHEYHBqy0Qe-8").server("EUW").rank("Gold IV").validated("pending")
                .validationCode("83ITES").validationTries(0).buildUser();
        database.addUser(user1);
        database.addUser(user2);
    }

    @Test
    public void getAllUsers() {
        List<User> users = database.getAllUsers();
        assertEquals("Database size is not 2.", users.size(), 2);
    }

    @Test
    public void getPendingUsers() {
        assertEquals("Database pending users are not 1.", database.getPendingUsers().size(), 1);
    }

    @Test
    public void getUserById() {
        List<User> users = database.getAllUsers();
        User user = database.getUserById(users.get(0).getId());
        assertEquals("Reddit name is not Thorasine.", user.getRedditName(), "Thorasine");
        assertEquals("Summoner name is not Trefort.", user.getSummonerName(), "Trefort");
    }

    @Test
    public void addUser() {
        User user = new UserBuilder().redditName("Thorasine").summonerName("Test").buildUser();
        database.addUser(user);
        assertEquals("Database size is not 3.", database.getAllUsers().size(), 3);
    }

    @Test
    public void deleteUser() {
        List<User> users = database.getAllUsers();
        database.deleteUser(users.get(0).getId());
        assertEquals("Database size is not 1.", database.getAllUsers().size(), 1);
        users = database.getAllUsers();
        assertEquals("Wrong account got deleted.", users.get(0).getSummonerName(), "Oreena");
    }

    @Test
    public void updateUser() {
        List<User> users = database.getAllUsers();
        User userTest1 = users.get(0);
        userTest1.setServer("NA");
        database.updateUser(userTest1);
        assertEquals("Users updated server is not NA", database.getUserById(userTest1.getId()).getServer(), "NA");
    }

    @Test
    public void batchUpdateUsersRank() {
        List<User> users = database.getAllUsers();
        User userTest1 = users.get(0);
        userTest1.setRank("Gold II");
        User userTest2 = users.get(1);
        userTest2.setRank("Silver IV");
        database.batchUpdateUsersRank(users);

        assertEquals("User1 is not Gold II", database.getUserById(users.get(0).getId()).getRank(), "Gold II");
        assertEquals("User2 is not Silver IV", database.getUserById(users.get(1).getId()).getRank(), "Silver IV");
    }

    @Test
    public void isSummonerAlreadyValidatedBySomeone() {
        assertTrue("Validated user Thorasine:Trefort not caught.", database.isSummonerAlreadyValidatedBySomeone(user1));
    }

    @Test
    public void isSummonerAlreadyRegisteredByUser() {
        assertTrue("Registered user Thorasine:Trefort not caught.", database.isSummonerAlreadyValidatedBySomeone(user1));
    }

    @Test
    public void getValidatedAccountsByServer() {
        List<User> users = database.getValidatedAccountsByServer("EUW");
        assertEquals("Validated users on EUW should be 1.", users.size(), 1);
    }

    @Test
    public void getValidatedAccountsByRedditName() {
        List<User> users = database.getValidatedAccountsByRedditName("Thorasine");
        assertEquals("Validated accounts for Thorasine should be 1.", users.size(), 1);
    }

    @Test
    public void getAccountsByRedditName() {
        List<User> users = database.getAccountsByRedditName("Thorasine");
        assertEquals("Thorasine's accounts should be 2", users.size(), 2);
    }
}