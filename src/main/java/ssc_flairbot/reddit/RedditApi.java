package ssc_flairbot.reddit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ssc_flairbot.persistence.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RedditApi {

    private String token;
    private final String subreddit = "myFlairTestSub";

    public void updateToken(String token) {
        this.token = token;
    }

    public void updateFlairs(List<User> users) {
        StringBuffer parameters = new StringBuffer("flair_csv=");
        for (User user : users) {
            parameters.append(user.getRedditName()).append(",").append(user.getRank()).append(",\n");
        }
        try {
            update(parameters.toString());
        } catch (Exception e) {
            Logger.getLogger(RedditApi.class.getName()).log(Level.WARNING, "Error while updating reddit flairs: " + e.getMessage());
            return;
        }
        Logger.getLogger(RedditApi.class.getName()).log(Level.INFO, "Updating " + users.size() + " reddit flairs has been successfully completed.");
    }

    private void update(String parameters) throws Exception {
        String url = "https://oauth.reddit.com/r/" + subreddit + "/api/flaircsv";
        URL object = new URL(url);
        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        String bearerAuth = "bearer " + token;
        con.setRequestProperty("Authorization", bearerAuth);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "windows:***REMOVED***:0.1 (by /u/Thorasine)");

        //String urlParameters = "flair_csv=Thorasine,Challenger," + "\n" + "Its_Vizicsacsi,Bronze II,";
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
