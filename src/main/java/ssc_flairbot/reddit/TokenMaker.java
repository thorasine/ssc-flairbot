package ssc_flairbot.reddit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handles the access token generating for reddit's REST API
 */
@Component
public class TokenMaker {

    private final Logger logger = Logger.getLogger(TokenMaker.class.getName());
    @Value("${reddit.client.modFlairRefreshToken}")
    private String refreshToken;
    @Value("${reddit.client.clientId}")
    private String clientId;
    @Value("${reddit.client.clientSecret}")
    private String clientSecret;

    /**
     * Generate an access token
     *
     * @return the access token
     */
    String getToken() {
        String token = null;
        try {
            token = sendTokenRequest();
            logger.log(Level.INFO, "Refreshed reddit token successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error refreshing reddit token: " + e.getMessage());
        }
        return token;
    }

    /**
     * Send a request to reddit's API to acquire a temporary token (with 1 hour lifespan)
     *
     * @return the access token
     * @throws Exception if the request have failed
     */
    private String sendTokenRequest() throws Exception {
        String url = "https://www.reddit.com/api/v1/access_token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.add("user-agent", "dev:flairbot:v1.0 (by /u/thorasine");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grant_type", "refresh_token")
                .queryParam("refresh_token", refreshToken);

        ResponseEntity<String> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String token = null;
        if (response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
            JSONObject json = new JSONObject(response.getBody());
            token = json.getString("access_token");
        } else throw new Exception("Something went wrong with the request. Status code: " + response.getStatusCode()
                + ", has body: " + response.hasBody());
        return token;
    }
}
