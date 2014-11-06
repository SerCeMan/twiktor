package ru.spbau.twiktor.app;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spbau.twiktor.Twiktor;
import ru.spbau.twiktor.transform.SearchSimularTransformer;
import ru.spbau.twiktor.transform.TwitTransformer;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

@Singleton
public class BotHandler {
    private final static Logger LOG = LoggerFactory.getLogger(BotHandler.class);

    private final ConcurrentHashMap<Integer, Twiktor> bots = new ConcurrentHashMap<>();
    private final String[] tags = {"#путин", "#россия", "#донбас"};
    private final List<String> themes = new ArrayList<>();

    @Inject
    Authorizator authorizator;

    public BotHandler() {
        themes.addAll(asList("Украина", "Путин", "Ургант"));
        try {
            String login = "WiktorGrishin";
            String token = "2862320699-yn8rZdX4g4wWFwnMm4BLdVgZ91kT8iAAiCLtYJB";
            String tokenSecret = "Y57QmqGqfh4pjkwynKIrLwcycXKNWxSDoXpom4HcvzAJ7";
            Twiktor twiktor = new Twiktor(login, createTrasformer(), tags,
                    new AccessToken(token, tokenSecret));
            bots.put(twiktor.getId(), twiktor);
        } catch (TwitterException e) {
            LOG.error("Imposible to create Twiktor ", e);
        }
    }

    public List<String> getThemes() {
        return themes;
    }

    public String beginAuth(String login) {
        return authorizator.beginAuth(login);
    }

    public void addBot(String login, String oauthVerifier) {
        try {
            AccessToken accessToken = authorizator.comleteAuth(login, oauthVerifier);
            Twiktor twiktor = new Twiktor(login, createTrasformer(), tags, accessToken);
            bots.put(twiktor.getId(), twiktor);
        } catch (TwitterException e) {
            LOG.error("Imposible to create Twiktor ", e);
        }
    }

    private TwitTransformer createTrasformer() {
        return new SearchSimularTransformer();
    }

    public void startBot(int id) {
        LOG.info("Starting bot ", id);
        bots.get(id).start();
    }

    public void stopBot(int id) {
        LOG.info("Stopping bot ", id);
        bots.get(id).stop();
    }

    public void sendToAll(String message) {
        LOG.info("Sending to all message: " + message);
        for (Twiktor twiktor : bots.values()) {
            try {
                twiktor.postStatus(message);
            } catch (TwitterException e) {
                LOG.error("Error sending message in " + twiktor.getLogin(), e);
            }
        }
    }

    public Collection<Twiktor> getAllTwiktors() {
        return bots.values();
    }

    public void addTheme(String theme) {
        themes.add(theme);
    }
}
