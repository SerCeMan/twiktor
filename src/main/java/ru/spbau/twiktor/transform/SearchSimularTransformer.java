package ru.spbau.twiktor.transform;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SearchSimularTransformer implements TwitTransformer {
	public static Status searchSimular(Status to, Twitter twitter) throws TwitterException {
		List<Status> result = twitter.search(new Query(to.getHashtagEntities()[0].getText())).getTweets();
		
		return result.get(ThreadLocalRandom.current().nextInt(result.size()));
	}

	@Override
	public String tranform(String statusText) {
		return null;
	}

	@Override
	public String tranform(Status status, Twitter twitter) {
		try {
			return searchSimular(status, twitter).getText();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
