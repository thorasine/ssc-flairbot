package ssc_flairbot;


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
import ssc_flairbot.league.LeagueApi;
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
public class AccountUpdaterTest {

    @Autowired
    AccountUpdater accountUpdater;
    @Autowired
    DBHandler database;
    @Autowired
    LeagueApi lolApi;

    private static int nAccounts = 5;
    private static boolean setupNeeded = true;
    private static List<User> euwUsers = new ArrayList<>();
    private static List<User> naUsers = new ArrayList<>();
    private static List<User> krUsers = new ArrayList<>();
    private static List<User> oceUsers = new ArrayList<>();

    @Before
    public void setUp() {
        if (setupNeeded) {
            setUsers("EUW", euwUsers);
            setUsers("NA", naUsers);
            setUsers("KR", krUsers);
            setUsers("OCE", oceUsers);
            setupNeeded = false;
        }
        database.dropTable();
        database.createTable();
    }

    //Reddit name is set to the summoner name too so often it'll result in "unable to resolve user" error from Reddit API
    private void setUsers(String server, List<User> list) {
        List<SpectatorParticipant> participants = new SpectatorBuilder().withPlatform(lolApi.platformConvert(server)).getFeaturedGames().get(0).getParticipants();
        for (int i = 0; i < nAccounts; i++) {
            Summoner summoner = new SummonerBuilder().withPlatform(lolApi.platformConvert(server)).withName(participants.get(i).getSummonerName()).get();
            User user = new UserBuilder().redditName(summoner.getName()).summonerName(summoner.getName())
                    .summonerId(summoner.getSummonerId()).server(server).validated("validated").buildUser();
            list.add(user);
        }
    }

    @Test
    public void isEveryoneUpdated() {
        for (int i = 0; i < nAccounts; i++) {
            database.addUser(euwUsers.get(i));
            database.addUser(naUsers.get(i));
            database.addUser(krUsers.get(i));
            database.addUser(oceUsers.get(i));
        }
        List<User> users = database.getAllUsers();
        users.forEach(user -> user.setRank("Iron I"));
        database.batchUpdateUsersRank(users);
        accountUpdater.scheduledUpdate();
        users = database.getAllUsers();
        users.forEach(user -> assertNotEquals("User's rank didn't get updated.", "Iron I", user.getRank()));
    }
}