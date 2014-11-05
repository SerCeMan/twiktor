package ru.spbau.twiktor;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spbau.twiktor.transform.TwitTransformer;
import ru.spbau.twiktor.transform.TwitTransformerImpl;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Twiktor {
	private final static Logger LOG = LoggerFactory.getLogger(Twiktor.class);
	private final static TwitTransformer TRANSFORMER = new TwitTransformerImpl();
	
	public static void main(String[] args) throws TwitterException {
		if (args.length == 0) {
			LOG.error("Twiktor needs at least one userId as argument");
			System.exit(0);
		}
		
		long usedUserId = getUserId(args);
		LOG.info("User Id to process: '{}'", usedUserId);
		
		Twitter twitter = getTwitter();
		long authUserId = twitter.getId();
		LOG.info("Twitter created. Used user id is '{}'", authUserId);
		
		Status status = getTwitText(twitter, usedUserId);
		String statusText = getText(status);
		LOG.info("Used status text is '{}'", statusText);
		
		String newText = TRANSFORMER.tranform(statusText);
		LOG.info("New text is '{}'", newText);
		
		twitter.updateStatus(newText);
	}

	private static String getText(Status status) {
		int columnPos = status.getText().indexOf(':');
		if (columnPos == -1) {
			return status.getText();
		}
		return status.getText().substring(columnPos + 1);
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
		twitter.setOAuthConsumer("ANV7A3SBGxsUz1z4LRETSfQCZ",
				"n82pOOT3vP9CJtRPrtm0qm7Y5DG2XXogteD1sQDezAGPGsI3bk");
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}

	// TODO load token
	private static AccessToken loadAccessToken() {
		String token = "2862320699-yn8rZdX4g4wWFwnMm4BLdVgZ91kT8iAAiCLtYJB";
		String tokenSecret = "Y57QmqGqfh4pjkwynKIrLwcycXKNWxSDoXpom4HcvzAJ7";
		return new AccessToken(token, tokenSecret);
	}
}
