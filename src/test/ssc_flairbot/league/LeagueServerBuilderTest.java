package ssc_flairbot.league;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class LeagueServerBuilderTest {

    private LeagueServerBuilder lsb;

    @Before
    public void setup() {
        lsb = new LeagueServerBuilder();
    }

    @Test
    public void getServers() {
        List<LeagueServer> servers = lsb.getServers(1);
        assertThat(servers).isNotNull();
        assertThat(servers.size()).isEqualTo(11);
    }
}