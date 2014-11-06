package ru.spbau.twiktor.app;

import com.google.inject.Singleton;
import ru.spbau.twiktor.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Отвечает за авторизацию ботов
 *
 * @author Sergey Tselovalnikov
 * @since 06.11.14
 */
@Singleton
public class Authorizator {

    private final ConcurrentHashMap<String, RequestToken> requests = new ConcurrentHashMap<>();


    public String beginAuth(String login) {
        try {
            Twitter twitter = createTwitter();
            RequestToken requestToken = twitter.getOAuthRequestToken(getCallbackURL(login));
            requests.put(login, requestToken);
            return requestToken.getAuthorizationURL();
        } catch (TwitterException e) {
            throw new RuntimeException("Imposible to begin auth of " + login, e);
        }
    }

    private Twitter createTwitter() {
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        return twitter;
    }

    public AccessToken comleteAuth(String login, String oauthVerifier) {
        try {
            return createTwitter().getOAuthAccessToken(requests.get(login), oauthVerifier);
        } catch (TwitterException e) {
            throw new RuntimeException("Imposible to complete auth of " + login, e);
        }
    }

    private String getCallbackURL(String login) {
        return Constants.ROOT + "complete-auth/" + login;
    }
}
