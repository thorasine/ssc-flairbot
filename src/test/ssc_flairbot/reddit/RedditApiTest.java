package ssc_flairbot.reddit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RedditApi.class, TokenMaker.class}, initializers = ConfigFileApplicationContextInitializer.class)
public class RedditApiTest {

    @Autowired
    RedditApi redditApi;

    @Test
    public void goodUpdateRankedFlairs() {
        Map<String,String> flairs = new HashMap<>();
        flairs.put("Thorasine", "Challenger I");
        flairs.put("Its_Vizicsacsi", "Platinum III");
        assertThat(redditApi.setFlairs((flairs))).isEqualTo("ok");
    }

    @Test
    public void updateNonExistingAccount() {
        Map<String,String> flairs = new HashMap<>();
        flairs.put("ThorasineNonExistingAccount", "Challenger I");
        assertThat(redditApi.setFlairs(flairs)).isNotEqualTo("ok");
    }
}