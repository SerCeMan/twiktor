package ru.spbau.twiktor;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Twiktor {
	private static Logger LOG = LoggerFactory.getLogger(Twiktor.class);
	
	public static void main(String[] args) throws TwitterException {
		if (args.length == 0) {
			LOG.error("Twiktor needs at least one userId as argument");
			System.exit(0);
		}
		
		long usedUserId = getUserId(args);
		LOG.info("User Id to process: {}", usedUserId);
		
		Twitter twitter = getTwitter();
		long authUserId = twitter.getId();
		LOG.info("Twitter created. Used user id is {}", authUserId);
		
		Status status = getTwitText(twitter, usedUserId);
		LOG.info("Used status text is {}", status.getText());
		
		// TODO convert status and post it
	}

	// TODO rewrite
	private static Status getTwitText(Twitter twitter, long usedUserId) throws TwitterException {
		// get last status
		Paging paging = new Paging(1, 1);
		Status status = twitter.getUserTimeline(usedUserId, paging).get(0);
		return status;
	}

	private static long getUserId(String[] args) {
		int userIdx = ThreadLocalRandom.current().nextInt(args.length);
		long userId = Long.parseLong(args[userIdx]);
		return userId;
	}

	private static Twitter getTwitter() {
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken();
		Twitter twitter = factory.getInstance();
		// TODO load secret
		twitter.setOAuthConsumer("UqpYMk89TqDN6aAqZHbm0FAf5",
				"HmtTMKzCb3DxCfzwgj4Ju1QnsAjaNaoST5fsIRVv3jIHxVUrMl");
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}

	// TODO load token
	private static AccessToken loadAccessToken() {
		String token = "2153109427-xS2HEtV8PlwopEglylOg4UaSosuiDX3Kqfn98Pr";
		String tokenSecret = "aOIl2e5dqmSF9gzV2wMA4CjyITOfIq3wiVK4wCCiUcO54";
		return new AccessToken(token, tokenSecret);
	}
}
