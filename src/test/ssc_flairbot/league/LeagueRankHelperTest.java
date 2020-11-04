package ssc_flairbot.league;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {LeagueRankHelper.class})
public class LeagueRankHelperTest {

    @Autowired
    private LeagueRankHelper rankHelper;

    @Test
    public void getHighestRank() {
        Set<String> ranks = new HashSet<>();
        ranks.add("Unranked");
        ranks.add("Iron I");
        ranks.add("Iron II");
        ranks.add("Bronze IV");
        ranks.add("Silver II");
        ranks.add("Gold II");
        ranks.add("Platinum I");
        ranks.add("Diamond III");
        ranks.add("Master I");
        ranks.add("Grandmaster I");
        ranks.add("Challenger I");
        assertThat(rankHelper.getHighestRank(ranks)).isEqualTo("Challenger I");
    }

    @Test
    public void getUnranked() {
        Set<String> ranks = new HashSet<>();
        assertThat(rankHelper.getHighestRank(ranks)).isEqualTo("Unranked");
    }
}