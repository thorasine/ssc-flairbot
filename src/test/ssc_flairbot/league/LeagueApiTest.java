package ssc_flairbot.league;

import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {LeagueApi.class, LeagueRankHelper.class})
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
        assertEquals("Summoner's name is not Thorasine.", "Thorasine", summoner.getName());
    }


    @Test
    public void getRank() {
        user1.setSummonerId(lolApi.getSummoner(user1).getSummonerId());
        assertNotNull("Highest rank for Thorasine is null.", lolApi.getRank(user1));
    }

}