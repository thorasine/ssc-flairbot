package ssc_flairbot.reddit;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ssc_flairbot.RateLimiter;
import ssc_flairbot.SecretFile;

import javax.annotation.PostConstruct;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handles communication with reddit's REST API.
 */

@Component
public class RedditApi {

    private final Logger logger = Logger.getLogger(RedditApi.class.getName());
    private final String SUBREDDIT = SecretFile.SUBREDDIT;
    private final String REDDIT_MOD_CLIENT_ID = SecretFile.REDDIT_MOD_CLIENT_ID;
    private final TokenMaker tokenMaker;
    private String token;
    private final RateLimiter limiter = new RateLimiter(60, 60_000);

    @Autowired
    public RedditApi(TokenMaker tokenMaker) {
        this.tokenMaker = tokenMaker;
    }

    @PostConstruct
    private void init() {
        refreshToken();
    }

    /**
     * Refresh the authorization token. The received token has one hour lifespan.
     */
    public void refreshToken() {
        this.token = tokenMaker.getToken();
    }

    /**
     * Set the reddit flairs for the given users.
     *
     * @param users the users whose flairs we want to set
     * @return a string about the success of the operation, used for tests
     */
    String setFlairs(Map<String, String> users) {
        String parameters = buildParameterString(users);
        try {
            limiter.acquire();
            limiter.enter();
            String answer = sendRequest(parameters);
            if (!answer.equalsIgnoreCase("ok")) {
                Logger.getLogger(RedditApi.class.getName()).log(Level.WARNING, "Some user flairs have failed to update:\n" + answer);
                return "warning";
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while updating reddit flairs: " + e.getMessage());
            return "error";
        }
        logger.log(Level.INFO, "Updating " + users.size() + " reddit flairs have been successfully completed.");
        return "ok";
    }

    /**
     * Build the parameters string for the reddit flair request.
     *
     * @param users the users whose flairs we want to send. Key is the user's reddit name, value is their rank in-game
     * @return parameters string ready to be send in a request
     */
    private String buildParameterString(Map<String, String> users) {
        StringBuilder parameters = new StringBuilder("flair_csv=");
        for (String redditName : users.keySet()) {
            parameters.append(redditName).append(",").append(users.get(redditName)).append(",\n");
        }
        return parameters.toString();
    }

    /**
     * Send a request to reddit's REST API to set up to 100 flairs.
     *
     * @param parameters the names and ranks of the users
     * @return "ok" string if the request was successful, an error message otherwise
     * @throws Exception if things have gone wrong with the request
     */
    private String sendRequest(String parameters) throws Exception {
        String url = "https://oauth.reddit.com/r/" + SUBREDDIT + "/api/flaircsv";
        URL object = new URL(url);
        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        String bearerAuth = "bearer " + token;
        con.setRequestProperty("Authorization", bearerAuth);
        con.setRequestProperty("User-Agent", "windows:" + REDDIT_MOD_CLIENT_ID + ":0.1 (by /u/Thorasine)");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }
        return readResponse(con);
    }

    /**
     * Read the HTTP response
     *
     * @param con the HTTP connector
     * @return "ok" if the request have been successful, an error string otherwise
     * @throws Exception if an exception has occurred during the reading
     */
    private String readResponse(HttpURLConnection con) throws Exception{
        StringBuilder response = new StringBuilder();
        JSONArray results = null;
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            results = convertToJson(br);
        }

        if (results == null) {
            response.append("Error during reading the response, Json has null value");
        } else {
            for (int i = 0; i < results.length(); i++) {
                JSONObject json = results.getJSONObject(i);
                if (!json.getBoolean("ok")) {
                    response.append(json.getString("errors")).append("\n");
                }
            }
        }
        return response.toString().length() == 0 ? "ok" : response.toString();
    }

    /**
     * Convert a BufferedReader object into a JSONArray
     * @param br the BufferedRead we read from
     * @return the JSONarray
     * @throws Exception if an exception has occurred during the conersion
     */
    private JSONArray convertToJson(BufferedReader br) throws Exception{
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = br.read()) != -1) {
            sb.append((char) cp);
        }
        String jsonText = sb.toString();
        return new JSONArray(jsonText);
    }
}
