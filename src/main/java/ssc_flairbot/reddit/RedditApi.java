package ssc_flairbot.reddit;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RedditApi {

    @Autowired
    private TokenMaker tokenMaker;

    private String token;
    private final String subreddit = "myFlairTestSub";
    //60 request / 1 min >> 60, 60_000
    private final RateLimiter limiter = new RateLimiter(60, 60_000);
    private final RateLimiter testlimiter = new RateLimiter(1, 60_000);

    @PostConstruct
    private void init() {
        refreshToken();
    }

    //Every 55 minutes
    @Scheduled(cron = "0 */55 * * * *")
    private void refreshToken() {
        this.token = tokenMaker.getToken();
    }

    public void testMethod(){
        Logger.getLogger(RedditApi.class.getName()).log(Level.INFO, "Started: Limit test.");
        testlimiter.acquire();
        testlimiter.enter();
        Logger.getLogger(RedditApi.class.getName()).log(Level.INFO, "Ended: Limit test.");
    }

    public void updateRankedFlairs(Map<String,String> users) {
        StringBuffer parameters = new StringBuffer("flair_csv=");
        for (String redditName : users.keySet()) {
            parameters.append(redditName).append(",").append(users.get(redditName)).append(",\n");
        }
        try {
            limiter.acquire();
            limiter.enter();
            update(parameters.toString());
        } catch (Exception e) {
            Logger.getLogger(RedditApi.class.getName()).log(Level.WARNING, "Error while updating reddit flairs: " + e.getMessage());
            return;
        }
        //Logger.getLogger(RedditApi.class.getName()).log(Level.INFO, "Updating " + users.size() + " reddit flairs have been successfully completed.");
    }

    public void updateFlairs(List<User> users) {
        StringBuffer parameters = new StringBuffer("flair_csv=");
        //100 lines total, the rest gets ignored by reddit
        for (User user : users) {
            parameters.append(user.getRedditName()).append(",").append(user.getRank()).append(",\n");
        }
        try {
            limiter.acquire();
            limiter.enter();
            update(parameters.toString());
        } catch (Exception e) {
            Logger.getLogger(RedditApi.class.getName()).log(Level.WARNING, "Error while updating reddit flairs: " + e.getMessage());
            return;
        }
        Logger.getLogger(RedditApi.class.getName()).log(Level.INFO, "Updating " + users.size() + " reddit flairs have been successfully completed.");
    }

    private void update(String parameters) throws Exception {
        String url = "https://oauth.reddit.com/r/" + subreddit + "/api/flaircsv";
        URL object = new URL(url);
        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        String bearerAuth = "bearer " + token;
        con.setRequestProperty("Authorization", bearerAuth);
        con.setRequestProperty("User-Agent", "windows:***REMOVED***:0.1 (by /u/Thorasine)");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }
        //Reading
        JSONArray results = readResults(con);
    }

    private JSONArray readResults(HttpURLConnection con) throws Exception {
        JSONArray json = null;
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            json = new JSONArray(jsonText);
            return json;
        }
        return null;
    }
}
