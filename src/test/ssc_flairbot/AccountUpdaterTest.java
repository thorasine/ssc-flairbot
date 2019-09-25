package ssc_flairbot;

import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
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

    private static boolean setUpIsDone = false;
    private final L4J8 riotApi = new L4J8(SecretFile.CREDS);
    private static List<User> euwUsers = new ArrayList<>();
    private static List<User> naUsers = new ArrayList<>();
    private static List<User> krUsers = new ArrayList<>();

    @Before
    public void setUp() {
        if (setUpIsDone) {
            database.dropTable();
            database.createTable();
            return;
        }
        setUsers("EUW", euwUsers);
        setUsers("NA", naUsers);
        setUsers("KR", krUsers);
        setUpIsDone = true;
        setUp();
    }

    private void setUsers(String server, List<User> list){
        List<SpectatorParticipant> participants = new SpectatorBuilder().withPlatform(lolApi.platformConvert(server)).getFeaturedGames().get(0).getParticipants();
        for (SpectatorParticipant p : participants) {
            Summoner summoner = new SummonerBuilder().withPlatform(lolApi.platformConvert(server)).withName(p.getSummonerName()).get();
            User user = new UserBuilder().redditName(summoner.getName()).summonerName(summoner.getName())
                    .summonerId(summoner.getSummonerId()).server(server).validated("validated").buildUser();
            list.add(user);
        }
    }

    @Test
    public void scheduledUpdate() {
        database.batchAddUsers(euwUsers);
        database.batchAddUsers(naUsers);
        database.batchAddUsers(krUsers);
        accountUpdater.scheduledUpdate();
    }
}