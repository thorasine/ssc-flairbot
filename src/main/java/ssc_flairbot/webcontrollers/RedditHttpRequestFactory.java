package ssc_flairbot.webcontrollers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Class that handles reddit Oath2 authorization.
 */
public class RedditHttpRequestFactory extends SimpleClientHttpRequestFactory {

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        ClientHttpRequest request = super.createRequest(uri, httpMethod);
        HttpHeaders headers = request.getHeaders();
        headers.add("User-Agent", "Thorasine flairbot login beta");
        headers.add("Connection", "keep-alive");
        return request;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        super.prepareConnection(connection, httpMethod);
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
    }
}