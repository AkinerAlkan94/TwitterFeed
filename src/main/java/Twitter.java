import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Twitter {
    RestTemplate restTemplate;
    Map<String, String> latestFetchedTweetIds = new HashMap<>();

    public Twitter() {
        restTemplate = new RestTemplate();
    }

    public String getUserTimeline(String userName, String userId) {
        String uri = "https://api.twitter.com/2/users/" + userId + "/tweets?max_results=5";
        uri = addSinceIdFromLastTweet(userName, uri);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(Utility.createHeaders()), String.class);
        return responseEntity.getBody();
    }

    private String addSinceIdFromLastTweet(String userName, String uri) {
        String latestTweetId = latestFetchedTweetIds.get(userName);
        if (latestTweetId != null) {
            uri = uri + "&since_id=" + latestTweetId;
        }
        return uri;
    }

    public void storeLatestTweet(String userName, AtomicBoolean isLatestPushed, String id) {
        if (!isLatestPushed.get()) {
            latestFetchedTweetIds.put(userName, id);
            isLatestPushed.set(true);
        }
    }
}
