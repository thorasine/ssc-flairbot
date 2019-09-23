package ssc_flairbot.reddit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssc_flairbot.SecretFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RedditApi {

    @Autowired
    private TokenMaker tokenMaker;

    private String token;
    private final String subreddit = SecretFile.SUBREDDIT;
    private final RateLimiter limiter = new RateLimiter(60, 60_000);

    @PostConstruct
    private void init() {
        refreshToken();
    }

    public void refreshToken() {
        this.token = tokenMaker.getToken();
    }

    //max 100 users at a time
    public String updateRankedFlairs(Map<String, String> users) {
        String response = null;
        StringBuilder parameters = new StringBuilder("flair_csv=");
        for (String redditName : users.keySet()) {
            parameters.append(redditName).append(",").append(users.get(redditName)).append(",\n");
        }
        try {
            limiter.acquire();
            limiter.enter();
            response = update(parameters.toString());
            if(!response.equalsIgnoreCase("ok")){
                Logger.getLogger(RedditApi.class.getName()).log(Level.WARNING, "Some user flairs are failed to update:\n" + response);
            }
        } catch (Exception e) {
            Logger.getLogger(RedditApi.class.getName()).log(Level.SEVERE, "Error while updating reddit flairs: " + e.getMessage());
            return e.getMessage();
        }
        Logger.getLogger(RedditApi.class.getName()).log(Level.FINE, "Updating " + users.size() + " reddit flairs have been successfully completed.");
        return response;
    }

    private String update(String parameters) throws Exception {
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
        StringBuilder response = new StringBuilder();
        JSONArray results = readResults(con);
        for (int i = 0; i < results.length(); i++) {
            JSONObject json = results.getJSONObject(i);
            if (!json.getString("ok").equalsIgnoreCase("true")) {
                response.append(json.getString("errors")).append("\n");
            }
        }
        return response.toString().length() == 0 ? "ok" : response.toString();
    }

    private JSONArray readResults(HttpURLConnection con) throws Exception {
        JSONArray jsonArray;
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            jsonArray = new JSONArray(jsonText);
            return jsonArray;
        }
        return null;
    }
}
