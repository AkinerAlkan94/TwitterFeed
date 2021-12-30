import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class TwitterFeed {
    Map<String, String> userIds;
    ObjectMapper objectMapper;
    Twitter twitter;

    public TwitterFeed(Map<String, String> users, Twitter twitter) {
        userIds = users;
        this.twitter = twitter;
        objectMapper = new ObjectMapper();
    }

    public void startLoop() throws JsonProcessingException {
        boolean isFirstCycle = true;
        System.out.println("Going To Start Watching All Users in UserIds in 5 Minutes");
        while (true) {
            System.out.println("Watching Tweets");
            for (Map.Entry<String, String> entry : userIds.entrySet()) {
                Utility.sleep(2000); // Wait 2 second between each call
                String userName = entry.getKey();
                String userId = entry.getValue();
                String resp = twitter.getUserTimeline(userName, userId);

                if (resp == null
                        || objectMapper.readTree(resp) == null
                        || objectMapper.readTree(resp).get("meta") == null
                        || objectMapper.readTree(resp).get("meta").get("result_count") == null) {
                    System.out.println("Bad Response For: " + userName);
                    continue;
                }

                int resultCount = getResultCount(resp);
                if (resultCount > 0) {
                    Iterator<JsonNode> tweets = getTweets(resp);
                    AtomicBoolean isLatestPushed = new AtomicBoolean(false);
                    boolean finalIsFirstCycle = isFirstCycle;
                    tweets.forEachRemaining(tweet -> processTweet(userName, isLatestPushed, finalIsFirstCycle, tweet));
                }
            }
            isFirstCycle = false;
            System.out.println("Sleeping for 5Min");
            Utility.sleep(1000 * 60 * 5);
        }
    }

    private void processTweet(String userName, AtomicBoolean isLatestPushed, boolean finalIsFirstCycle, JsonNode tweet) {
        String id = tweet.get("id").textValue();
        String text = tweet.get("text").textValue();
        if (!finalIsFirstCycle) {
            String title = "***" + userName + "***" + "\n";
            String link = "Twitter Link: " + "https://twitter.com/" + userName + "/status/" + id + "\n";
            Telegram.sendMessageToGroup(title + link + text);
        }

        twitter.storeLatestTweet(userName, isLatestPushed, id);
    }


    private Iterator<JsonNode> getTweets(String resp) throws JsonProcessingException {
        return objectMapper.readTree(resp).get("data").elements();
    }

    private int getResultCount(String resp) throws JsonProcessingException {
        return objectMapper.readTree(resp).get("meta").get("result_count").intValue();
    }
}
