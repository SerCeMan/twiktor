package ru.spbau.twiktor.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Query.ResultType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        List<Status> result = new ArrayList<>();
        QueryResult qRes = twitter.search(new Query(tag).count(100).count(100).resultType(ResultType.recent));
        do {
        	result.addAll(qRes.getTweets());
        	qRes = twitter.search(qRes.nextQuery());
        } while(result.size() < 200 && qRes.hasNext());

        return result.get(ThreadLocalRandom.current().nextInt(result.size()));
    }
}
