package ssc_flairbot.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DBHandler.class})
public class DBHandlerTest {

    @Autowired
    private DBHandler database;

    private User user1;
    private User user2;

    @Before
    public void setUp() {
        database.dropTable();
        database.createTable();
        user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("fakeSummonerId1").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERFK").validationTries(0).buildUser();
        user2 = new UserBuilder().redditName("Thorasine").summonerName("Oreena")
                .summonerId("fakeSummonerID2").server("EUW").rank("Gold IV").validated("pending")
                .validationCode("QWERTY").validationTries(0).buildUser();
        database.addUser(user1);
        database.addUser(user2);
        List<User> allUsers = database.getAllUsers();
        user1 = database.getUserById(allUsers.get(0).getId());
        user2 = database.getUserById(allUsers.get(1).getId());
    }

    @Test
    public void getAllUsers() {
        List<User> users = database.getAllUsers();
        assertEquals("Database size is not 2.", 2, users.size());
    }

    @Test
    public void getPendingUsers() {
        assertEquals("Database pending users are not 1.", 1, database.getPendingUsers().size());
    }

    @Test
    public void getUserById() {
        User user = database.getUserById(user1.getId());
        assertEquals("Reddit name is not Thorasine.", "Thorasine", user.getRedditName());
        assertEquals("Summoner name is not Trefort.", "Trefort", user.getSummonerName());
    }

    @Test
    public void addUser() {
        User user3 = new UserBuilder().redditName("Pilvax").summonerName("Sniblets")
                .summonerId("fakeSummonerId3").server("OCE").rank("Master I").validated("validated")
                .validationCode("AZERTY").validationTries(3).buildUser();
        database.addUser(user3);
        User testUser = database.getAllUsers().get(2);
        assertEquals("Database size is not 3.", 3, database.getAllUsers().size());
        assertEquals("Reddit name is not Pilvax.", "Pilvax", testUser.getRedditName());
        assertEquals("Summoner name is not Sniblets.", "Sniblets", testUser.getSummonerName());
        assertEquals("SummonerId is not fakeSummonerId3", "fakeSummonerId3", testUser.getSummonerId());
        assertEquals("Server is not OCE", "OCE", testUser.getServer());
        assertEquals("Rank is not Master I", "Master I", testUser.getRank());
        assertEquals("Validated is not validated", "validated", testUser.getValidated());
        assertEquals("Validation code is not AZERTY", "AZERTY", testUser.getValidationCode());
        assertEquals("Validation tries is not 3", 3, testUser.getValidationTries());
    }

    @Test
    public void batchAddUser(){
        User user3 = new UserBuilder().redditName("Pilvax").summonerName("Sniblets")
                .summonerId("fakeSummonerId3").server("OCE").rank("Master I").validated("validated")
                .validationCode("AZERTY").validationTries(3).buildUser();
        User user4 = new UserBuilder().redditName("George").summonerName("Charlie")
                .summonerId("fakeSummonerId4").server("JP").rank("Grandmaster I").validated("validated")
                .validationCode("QWERTZ").validationTries(5).buildUser();
        List<User> testUsers = new ArrayList<>();
        testUsers.add(user3);
        testUsers.add(user4);
        database.batchAddUsers(testUsers);

        User testUser3 = database.getAllUsers().get(2);
        User testUser4 = database.getAllUsers().get(3);
        assertEquals("Database size is not 3.", 4, database.getAllUsers().size());
        assertEquals("Reddit name is not Pilvax.", "Pilvax", testUser3.getRedditName());
        assertEquals("Summoner name is not Sniblets.", "Sniblets", testUser3.getSummonerName());
        assertEquals("SummonerId is not fakeSummonerId3", "fakeSummonerId3", testUser3.getSummonerId());
        assertEquals("Server is not OCE", "OCE", testUser3.getServer());
        assertEquals("Rank is not Master I", "Master I", testUser3.getRank());
        assertEquals("Validated is not validated", "validated", testUser3.getValidated());
        assertEquals("Validation code is not AZERTY", "AZERTY", testUser3.getValidationCode());
        assertEquals("Validation tries is not 3", 3, testUser3.getValidationTries());

        assertEquals("Reddit name is not George.", "George", testUser4.getRedditName());
        assertEquals("Summoner name is not Charlie.", "Charlie", testUser4.getSummonerName());
        assertEquals("SummonerId is not fakeSummonerId4", "fakeSummonerId4", testUser4.getSummonerId());
        assertEquals("Server is not JP", "JP", testUser4.getServer());
        assertEquals("Rank is not Grandmaster I", "Grandmaster I", testUser4.getRank());
        assertEquals("Validated is not validated", "validated", testUser4.getValidated());
        assertEquals("Validation code is not QWERTZ", "QWERTZ", testUser4.getValidationCode());
        assertEquals("Validation tries is not 5", 5, testUser4.getValidationTries());
    }

    @Test
    public void deleteUser() {
        database.deleteUser(user1.getId());
        assertEquals("Database size is not 1.", 1, database.getAllUsers().size());
        List<User> users = database.getAllUsers();
        assertEquals("Wrong account got deleted.", "Oreena", users.get(0).getSummonerName());
    }

    @Test
    public void updateUser() {
        User user = database.getUserById(user1.getId());
        user.setServer("NA");
        database.updateUser(user);
        assertEquals("Users updated server is not NA", "NA", database.getUserById(user.getId()).getServer());
    }

    @Test
    public void batchUpdateUsersRank() {
        List<User> users = database.getAllUsers();
        User userTest1 = users.get(0);
        userTest1.setRank("Gold II");
        User userTest2 = users.get(1);
        userTest2.setRank("Silver IV");
        database.batchUpdateUsersRank(users);

        assertEquals("User1 is not Gold II", "Gold II", database.getUserById(users.get(0).getId()).getRank());
        assertEquals("User2 is not Silver IV", "Silver IV", database.getUserById(users.get(1).getId()).getRank());
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
        assertEquals("Validated users on EUW should be 1.", 1, users.size());
    }

    @Test
    public void getValidatedAccountsByRedditName() {
        List<User> users = database.getValidatedAccountsByRedditName("Thorasine");
        assertEquals("Validated accounts for Thorasine should be 1.", 1, users.size());
    }

    @Test
    public void getAccountsByRedditName() {
        List<User> users = database.getAccountsByRedditName("Thorasine");
        assertEquals("Thorasine's accounts should be 2", 2, users.size());
    }
}