package ssc_flairbot.league;

import no.stelar7.api.l4j8.basic.constants.api.Platform;
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

    private User user;

    @Before
    public void setUp() {
        user = new UserBuilder().redditName("Thorasine").summonerName("Thorasine").server("EUW").buildUser();
    }

    @Test
    public void getSummoner() {
        Summoner summoner = lolApi.getSummoner(user);
        assertEquals("Summoner's name is not Thorasine.", "Thorasine", summoner.getName());
    }

    @Test
    public void getRank() {
        user.setSummonerId(lolApi.getSummoner(user).getSummonerId());
        assertNotNull("Highest rank for Thorasine is null.", lolApi.getRank(user));
    }

    @Test
    public void getThirdPartyCode(){
        user.setSummonerId(lolApi.getSummoner(user).getSummonerId());
        assertNotNull("Third party code for Thorasine is null", lolApi.getThirdPartyCode(user));
    }

    @Test
    public void getRightPlatform(){
        Platform platform = lolApi.platformConvert("EUW");
        assertEquals("Platform converter returns wrong result.", Platform.EUW1, platform);
    }

}