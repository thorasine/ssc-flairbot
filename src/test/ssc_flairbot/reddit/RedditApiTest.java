package ssc_flairbot.reddit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RedditApi.class, TokenMaker.class})
public class RedditApiTest {

    @Autowired
    RedditApi redditApi;

    @Test
    public void goodUpdateRankedFlairs() {
        Map<String,String> flairs = new HashMap<>();
        flairs.put("Thorasine", "Challenger I");
        flairs.put("Its_Vizicsacsi", "Platinum III");
        assertEquals("Flair update failed.", "ok", redditApi.setFlairs(flairs));
    }

    @Test
    public void updateNonExistingAccount() {
        Map<String,String> flairs = new HashMap<>();
        flairs.put("ThorasineNonExistingAccount", "Challenger I");
        assertNotEquals("Flair update not failed.", "ok", redditApi.setFlairs(flairs));
    }
}