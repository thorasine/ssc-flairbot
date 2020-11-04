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

import static org.assertj.core.api.Assertions.assertThat;

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
    public void hasSummonerName() {
        Summoner summoner = lolApi.getSummoner(user);
        assertThat(summoner).isNotNull();
        assertThat(summoner.getName()).isNotNull();
    }

    @Test
    public void hasSummonerRank() {
        user.setSummonerId(lolApi.getSummoner(user).getSummonerId());
        assertThat(lolApi.getRank(user)).isNotNull();
    }

    @Test
    public void hasNoSummonerName() {
        user = new UserBuilder().summonerName("this summoner name cannot exist").server("EUW").buildUser();
        Summoner summoner = lolApi.getSummoner(user);
        assertThat(summoner).isNull();
    }

    @Test
    public void hasThirdPartyCode() {
        user.setSummonerId(lolApi.getSummoner(user).getSummonerId());
        assertThat(lolApi.getThirdPartyCode(user)).isNotNull();
    }

    @Test
    public void getRightPlatform() {
        Platform platform = lolApi.platformConvert("EUW");
        assertThat(platform).isEqualTo(Platform.EUW1);
        platform = lolApi.platformConvert("NA");
        assertThat(platform).isEqualTo(Platform.NA1);
        platform = lolApi.platformConvert("EUNE");
        assertThat(platform).isEqualTo(Platform.EUN1);
        platform = lolApi.platformConvert("BR");
        assertThat(platform).isEqualTo(Platform.BR1);
        platform = lolApi.platformConvert("LAN");
        assertThat(platform).isEqualTo(Platform.LA1);
        platform = lolApi.platformConvert("LAS");
        assertThat(platform).isEqualTo(Platform.LA2);
        platform = lolApi.platformConvert("JP");
        assertThat(platform).isEqualTo(Platform.JP1);
        platform = lolApi.platformConvert("KR");
        assertThat(platform).isEqualTo(Platform.KR);
        platform = lolApi.platformConvert("OCE");
        assertThat(platform).isEqualTo(Platform.OC1);
        platform = lolApi.platformConvert("RU");
        assertThat(platform).isEqualTo(Platform.RU);
        platform = lolApi.platformConvert("TR");
        assertThat(platform).isEqualTo(Platform.TR1);
    }

}