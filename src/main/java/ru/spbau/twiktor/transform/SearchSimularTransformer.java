package ru.spbau.twiktor.transform;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.twiktor.Constants;
import twitter4j.*;

public class SearchSimularTransformer implements TwitTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(SearchSimularTransformer.class);

    @Override
    public String tranform(String statusText) {
        return null;
    }

    @Override
    public String tranform(Status status, Twitter twitter, String tag) {
        try {
            LOG.info("Search twit for tag=" + tag);
            return searchSimular(status, twitter, tag).getText();
        } catch (Exception e) {
            throw new RuntimeException("Impossible to transform twit", e);
        }
    }

    public Status searchSimular(Status to, Twitter twitter, String tag) throws TwitterException {
        List<Status> result = twitter.search(new Query(tag)).getTweets();

        return result.get(ThreadLocalRandom.current().nextInt(result.size()));
    }
}
