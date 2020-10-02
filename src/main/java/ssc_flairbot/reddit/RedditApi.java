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

@Component
public class RedditApi {

	private final TokenMaker tokenMaker;

	private String token;

	private final String subreddit = SecretFile.SUBREDDIT;

	private final String redditModClientId = SecretFile.REDDIT_MOD_CLIENT_ID;

	private final RateLimiter limiter = new RateLimiter(60, 60_000);

	@Autowired
	public RedditApi(TokenMaker tokenMaker) {
		this.tokenMaker = tokenMaker;
	}

	@PostConstruct
	private void init() {
		refreshToken();
	}

	public void refreshToken() {
		this.token = tokenMaker.getToken();
	}

	//The api can handle max 100 users at a time
	public String updateRankedFlairs(Map<String, String> users) {
		StringBuilder parameters = new StringBuilder("flair_csv=");
		for (String redditName : users.keySet()) {
			parameters.append(redditName).append(",").append(users.get(redditName)).append(",\n");
		}
		try {
			limiter.acquire();
			limiter.enter();
			String answer = update(parameters.toString());
			if (!answer.equalsIgnoreCase("ok")) {
				Logger.getLogger(RedditApi.class.getName()).log(Level.WARNING, "Some user flairs have failed to update:\n" + answer);
				return "warning";
			}
		}
		catch (Exception e) {
			Logger.getLogger(RedditApi.class.getName()).log(Level.SEVERE, "Error while updating reddit flairs: " + e.getMessage());
			return "error";
		}
		Logger.getLogger(RedditApi.class.getName()).log(Level.FINE, "Updating " + users.size() + " reddit flairs have been successfully completed.");
		return "ok";
	}

	private String update(String parameters) throws Exception {
		String url = "https://oauth.reddit.com/r/" + subreddit + "/api/flaircsv";
		URL object = new URL(url);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		String bearerAuth = "bearer " + token;
		con.setRequestProperty("Authorization", bearerAuth);
		con.setRequestProperty("User-Agent", "windows:" + redditModClientId + ":0.1 (by /u/Thorasine)");
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
