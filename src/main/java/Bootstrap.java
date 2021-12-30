import java.io.IOException;

public class Bootstrap {

    public static void main(String[] args) throws IOException {
        UserIds userIds = new UserIds();
        Twitter twitter = new Twitter();
        TwitterFeed twitterFeed = new TwitterFeed(userIds.getUserIds(),twitter);
        twitterFeed.startLoop();
    }
}
