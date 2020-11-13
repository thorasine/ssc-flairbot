package ssc_flairbot.reddit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ssc_flairbot.RateLimiter;
import ssc_flairbot.SecretFile;

import javax.annotation.PostConstruct;
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
    private final TokenMaker tokenMaker;
    private String token;
    //Reddit API limit
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
            String answer = sendFlairRequest(parameters);
            if (!answer.equals("ok")) {
                logger.log(Level.WARNING, "Some user flairs have failed to update:\n" + answer);
                return "warning";
            }
            logger.log(Level.INFO, "Updating " + users.size() + " reddit flairs have been successfully completed.");
            return "ok";
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while updating reddit flairs: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Build the parameters string for the reddit flair request.
     *
     * @param users the users whose flairs we want to send. Key is the user's reddit name, value is their rank in-game
     * @return parameters string ready to be send in a request
     */
    private String buildParameterString(Map<String, String> users) {
        StringBuilder parameters = new StringBuilder();
        for (String redditName : users.keySet()) {
            parameters.append(redditName).append(",").append(users.get(redditName)).append(",\n");
        }
        return parameters.toString();
    }

    /**
     * Send a request to reddit's REST API to set up to 100 flairs. Retun a string based on the result of the operation
     *
     * @param parameters the names and ranks of the users
     * @return "ok" string if all the updates successful, an error string and description otherwise
     * @throws Exception if the request or reading have failed
     */
    private String sendFlairRequest(String parameters) throws Exception {
        String url = "https://oauth.reddit.com/r/" + SUBREDDIT + "/api/flaircsv?flair_csv=";
        url += parameters;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add("user-agent", "dev:flairbot:v1.0 (by /u/thorasine");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        return readResponse(response);
    }

    /**
     * Read the response of the request and return a string based on the result of the operation
     *
     * @param response the response we got from the API request
     * @return "ok" string if all the updates successful, an error string and description otherwise
     * @throws Exception if the request or reading of the response have failed
     */
    private String readResponse(ResponseEntity<String> response) throws Exception {
        StringBuilder result = new StringBuilder();
        if (response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
            JSONArray jarray = new JSONArray(response.getBody());
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject json = jarray.getJSONObject(i);
                if (!json.getBoolean("ok")) {
                    result.append(json.getString("errors")).append("\n");
                }
            }
        } else
            throw new Exception("Something went wrong with the request. Status code: " + response.getStatusCode()
                    + ", has body: " + response.hasBody());
        return result.toString().length() == 0 ? "ok" : result.toString();
    }


}
