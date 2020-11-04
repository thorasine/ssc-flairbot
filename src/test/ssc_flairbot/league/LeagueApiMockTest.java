package ssc_flairbot.league;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ssc_flairbot.persistence.User;
import ssc_flairbot.persistence.UserBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LeagueApiMockTest {

    @Mock
    private LeagueRankHelper leagueRankHelper;

    @InjectMocks
    private LeagueApi lolApi;

    private User user;

    @Before
    public void setup() {
        lolApi.init();
        user = new UserBuilder().redditName("Thorasine").summonerName("Thorasine").server("EUW").buildUser();
    }

    @Test
    public void hasRank() {
        when(leagueRankHelper.get5v5SoloRank(anyList())).thenReturn("Diamond I");
        user.setSummonerId(lolApi.getSummoner(user).getSummonerId());
        assertThat(lolApi.getRank(user)).isNotNull();
    }

}