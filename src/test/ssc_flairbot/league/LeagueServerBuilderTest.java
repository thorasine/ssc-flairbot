package ssc_flairbot.league;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {LeagueServerBuilder.class})
public class LeagueServerBuilderTest {

    private LeagueServerBuilder lsb;

    @Before
    public void setup(){
        lsb = new LeagueServerBuilder();
    }

    @Test
    public void getServers() {
        List<LeagueServer> servers = lsb.getServers(0.5);
    }
}