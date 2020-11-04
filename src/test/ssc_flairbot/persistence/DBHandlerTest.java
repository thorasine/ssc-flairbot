package ssc_flairbot.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@JdbcTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DBHandler.class})
public class DBHandlerTest {

    @Autowired
    private DBHandler db;

    private User user1;
    private User user2;

    @Before
    public void setUp() {
        db.dropTable();
        db.createTable();
        user1 = new UserBuilder().redditName("Thorasine").summonerName("Trefort")
                .summonerId("fakeSummonerId1").server("EUW").rank("Diamond I").validated("validated")
                .validationCode("83ERFK").validationTries(0).buildUser();
        user2 = new UserBuilder().redditName("Thorasine").summonerName("Oreena")
                .summonerId("fakeSummonerID2").server("EUW").rank("Gold IV").validated("pending")
                .validationCode("QWERTY").validationTries(0).buildUser();
        db.addUser(user1);
        db.addUser(user2);
        List<User> allUsers = db.getAllUsers();
        user1 = db.getUserById(allUsers.get(0).getId());
        user2 = db.getUserById(allUsers.get(1).getId());
    }

    @Test
    public void getAllUsers() {
        List<User> users = db.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void getPendingUsers() {
        assertThat(db.getPendingUsers().size()).isEqualTo(1);
    }

    @Test
    public void getUserById() {
        User user = db.getUserById(user1.getId());
        assertThat(user.getRedditName()).isEqualTo("Thorasine");
        assertThat(user.getSummonerName()).isEqualTo("Trefort");
    }

    @Test
    public void addUser() {
        User user3 = new UserBuilder().redditName("Pilvax").summonerName("Sniblets")
                .summonerId("fakeSummonerId3").server("OCE").rank("Master I").validated("validated")
                .validationCode("AZERTY").validationTries(3).buildUser();
        db.addUser(user3);
        User testUser = db.getAllUsers().get(2);
        assertThat(db.getAllUsers().size()).isEqualTo(3);
        assertThat(testUser.getRedditName()).isEqualTo("Pilvax");
        assertThat(testUser.getSummonerName()).isEqualTo("Sniblets");
        assertThat(testUser.getSummonerId()).isEqualTo("fakeSummonerId3");
        assertThat(testUser.getServer()).isEqualTo("OCE");
        assertThat(testUser.getRank()).isEqualTo("Master I");
        assertThat(testUser.getValidated()).isEqualTo("validated");
        assertThat(testUser.getValidationCode()).isEqualTo("AZERTY");
        assertThat(testUser.getValidationTries()).isEqualTo(3);
    }

    @Test
    public void batchAddUser() {
        User user3 = new UserBuilder().redditName("Pilvax").summonerName("Sniblets")
                .summonerId("fakeSummonerId3").server("OCE").rank("Master I").validated("validated")
                .validationCode("AZERTY").validationTries(3).buildUser();
        User user4 = new UserBuilder().redditName("George").summonerName("Charlie")
                .summonerId("fakeSummonerId4").server("JP").rank("Grandmaster I").validated("validated")
                .validationCode("QWERTZ").validationTries(5).buildUser();
        List<User> testUsers = new ArrayList<>();
        testUsers.add(user3);
        testUsers.add(user4);
        db.batchAddUsers(testUsers);

        User testUser3 = db.getAllUsers().get(2);
        User testUser4 = db.getAllUsers().get(3);
        assertThat(db.getAllUsers().size()).isEqualTo(4);

        assertThat(testUser3.getRedditName()).isEqualTo("Pilvax");
        assertThat(testUser3.getSummonerName()).isEqualTo("Sniblets");
        assertThat(testUser3.getSummonerId()).isEqualTo("fakeSummonerId3");
        assertThat(testUser3.getServer()).isEqualTo("OCE");
        assertThat(testUser3.getRank()).isEqualTo("Master I");
        assertThat(testUser3.getValidated()).isEqualTo("validated");
        assertThat(testUser3.getValidationCode()).isEqualTo("AZERTY");
        assertThat(testUser3.getValidationTries()).isEqualTo(3);

        assertThat(testUser4.getRedditName()).isEqualTo("George");
        assertThat(testUser4.getSummonerName()).isEqualTo("Charlie");
        assertThat(testUser4.getSummonerId()).isEqualTo("fakeSummonerId4");
        assertThat(testUser4.getServer()).isEqualTo("JP");
        assertThat(testUser4.getRank()).isEqualTo("Grandmaster I");
        assertThat(testUser4.getValidated()).isEqualTo("validated");
        assertThat(testUser4.getValidationCode()).isEqualTo("QWERTZ");
        assertThat(testUser4.getValidationTries()).isEqualTo(5);
    }

    @Test
    public void deleteUser() {
        db.deleteUser(user1.getId());
        assertThat(db.getAllUsers().size()).isEqualTo(1);
        List<User> users = db.getAllUsers();
        assertThat(users.get(0).getSummonerName()).isEqualTo("Oreena");
    }

    @Test
    public void updateUser() {
        User user = db.getUserById(user1.getId());
        user.setServer("NA");
        db.updateUser(user);
        assertThat(db.getUserById(user.getId()).getServer()).isEqualTo("NA");
    }

    @Test
    public void batchUpdateUsersRank() {
        List<User> users = db.getAllUsers();
        User userTest1 = users.get(0);
        userTest1.setRank("Gold II");
        User userTest2 = users.get(1);
        userTest2.setRank("Silver IV");
        db.batchUpdateUsersRank(users);

        assertThat(db.getUserById(users.get(0).getId()).getRank()).isEqualTo("Gold II");
        assertThat(db.getUserById(users.get(1).getId()).getRank()).isEqualTo("Silver IV");
    }

    @Test
    public void isSummonerAlreadyValidatedBySomeone() {
        assertThat(db.isSummonerAlreadyValidatedBySomeone(user1)).isTrue();
    }

    @Test
    public void isSummonerAlreadyRegisteredByUser() {
        assertThat(db.isSummonerAlreadyRegisteredByUser(user1)).isTrue();
        //assertTrue("Registered user Thorasine:Trefort not caught.", db.isSummonerAlreadyValidatedBySomeone(user1));
    }

    @Test
    public void getValidatedAccountsByServer() {
        List<User> users = db.getValidatedAccountsByServer("EUW");
        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    public void getValidatedAccountsByRedditName() {
        List<User> users = db.getValidatedAccountsByRedditName("Thorasine");
        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    public void getAccountsByRedditName() {
        List<User> users = db.getAccountsByRedditName("Thorasine");
        assertThat(users.size()).isEqualTo(2);
    }
}