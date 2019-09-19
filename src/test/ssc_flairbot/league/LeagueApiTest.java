package ssc_flairbot.league;

import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ComponentScan
@ImportAutoConfiguration
public class LeagueApiTest {

    @Autowired
    private LeagueApi lolApi;

    private User user1;

    @Before
    public void setUp() {
        user1 = new UserBuilder().redditName("Thorasine").summonerName("Thorasine").server("EUW").buildUser();
    }

    @Test
    public void getSummoner() {
        Summoner summoner = lolApi.getSummoner(user1);
        assertEquals("Summoner's name is not Thorasine.", summoner.getName(), "Thorasine");
    }


    @Test
    public void getHighestRank() {
        user1.setSummonerId(lolApi.getSummoner(user1).getSummonerId());
        assertNotNull("Highest rank for Thorasine is null.", lolApi.getHighestRank(user1));
    }

}