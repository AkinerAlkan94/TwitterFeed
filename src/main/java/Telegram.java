import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Telegram {

    public static void sendMessageToGroup(String message) {
        try {
            URL url = new URL(String.format(Constants.TELEGRAM_CHAT_URL, Constants.TELEGRAM_CHAT_BOT_TOKEN, Constants.TELEGRAM_CHAT_GROUP_ID, encodeValue(message)));
            URLConnection conn = url.openConnection();
            new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception ex) {
            return "Failed to Encode";
        }
    }
}
