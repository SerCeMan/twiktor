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

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

@Singleton
public class BotHandler {
    private final static Logger LOG = LoggerFactory.getLogger(BotHandler.class);

    private final ConcurrentHashMap<Integer, Twiktor> bots = new ConcurrentHashMap<>();
    private final List<String> themes = new ArrayList<>();

    @Inject
    Authorizator authorizator;

    public BotHandler() {
        loadThemes();
        loadBots();
    }

    private void loadThemes() {
        try(InputStream input = new FileInputStream("themes.txt")) {
            InputStreamReader reader = new InputStreamReader(input);
            Scanner scanner = new Scanner(reader);
            scanner.forEachRemaining(themes::add);
        } catch (Exception e) {
            LOG.error("Error load themes ", e);
        }
    }

    private void loadBots() {
        try(InputStream input = new FileInputStream("twiktors.txt")) {
            InputStreamReader reader = new InputStreamReader(input);
            Scanner scanner = new Scanner(reader);
            int count = Integer.valueOf(scanner.nextLine());
            for(int i = 0; i < count; i++) {
                String login = scanner.nextLine();
                String token = scanner.nextLine();
                String tokenSecret = scanner.nextLine();
                try {
                    Twiktor twiktor = new Twiktor(login, createTrasformer(), themes.toArray(new String[]{}),
                            new AccessToken(token, tokenSecret));
                    bots.put(twiktor.getId(), twiktor);
                } catch (Exception e) {
                    LOG.info("Error loading " + login);
                }
            }
        } catch (Exception e) {
            LOG.error("Error load themes ", e);
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
            Twiktor twiktor = new Twiktor(login, createTrasformer(), themes.toArray(new String[]{}), accessToken);
            bots.put(twiktor.getId(), twiktor);
        } catch (TwitterException e) {
            LOG.error("Imposible to create Twiktor ", e);
        }
        saveTwiktors();
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
        String[] tags = themes.toArray(new String[]{});
        for(Twiktor tw: bots.values()) {
        	tw.setTags(tags);
        }
        saveThemes();
    }

    private void saveThemes() {
        try(FileOutputStream out = new FileOutputStream("themes.txt")) {
            PrintWriter writer = new PrintWriter(out);
            themes.forEach(writer::println);
            writer.flush();
        } catch (Exception e) {
            LOG.error("Error saving themes ", e);
        }
    }

    private void saveTwiktors() {
        try(FileOutputStream out = new FileOutputStream("twiktors.txt")) {
            PrintWriter writer = new PrintWriter(out);
            writer.println(bots.values().size());
            bots.values().forEach(bot -> {
                writer.println(bot.getLogin());
                writer.println(bot.getAccessToken().getToken());
                writer.println(bot.getAccessToken().getTokenSecret());
            });
            writer.flush();
        } catch (Exception e) {
            LOG.error("Error saving themes ", e);
        }
    }

    public void delTheme(String theme) {
        themes.remove(theme);
        String[] tags = themes.toArray(new String[]{});
        for(Twiktor tw: bots.values()) {
            tw.setTags(tags);
        }
        saveThemes();
    }
}
