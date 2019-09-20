package ssc_flairbot.reddit;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ssc_flairbot.SecretFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class TokenMaker {

    private final String refreshToken = SecretFile.REDDIT_REFRESH_TOKEN;

    public String getToken(){
        String token = null;
        try {
            token = refreshToken();
        } catch (Exception e) {
            Logger.getLogger(TokenMaker.class.getName()).log(Level.SEVERE,"Error refreshing reddit token: " + e.getMessage());
        }
        Logger.getLogger(TokenMaker.class.getName()).log(Level.INFO,"Refreshed reddit token successfully.");
        return token;
    }

    public String refreshToken() throws Exception {
        String url = "https://www.reddit.com/api/v1/access_token";
        URL object = new URL(url);
        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        String userCredentials = "***REMOVED***:***REMOVED***";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        con.setRequestProperty("Authorization", basicAuth);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Thorasine Flairbotmod testing");

        String urlParameters = "grant_type=refresh_token&refresh_token=" + refreshToken;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }

        //Reading
        String token = readResults(con).getString("access_token");
        return token;
    }

    private JSONObject readResults(HttpURLConnection con) throws Exception{
        JSONObject json = null;
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            String jsonText = sb.toString();
            json = new JSONObject(jsonText);
            return json;
        }
        return json;
    }
}
