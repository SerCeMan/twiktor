package ru.spbau.twiktor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spbau.twiktor.transform.TwitTransformer;
import ru.spbau.twiktor.transform.TwitTransformerImpl;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Twiktor {
	private final static Logger LOG = LoggerFactory.getLogger(Twiktor.class);
	private final static TwitTransformer TRANSFORMER = new TwitTransformerImpl();
	private static Twitter twitter;
	
	public static void main(String[] args) throws IllegalStateException, TwitterException {
		twitter = getTwitter();
		
		long authUserId = twitter.getId();
		LOG.info("Twitter created. Used user id is '{}'", authUserId);
		
		while(true) {
			try {
				run(args);
				Thread.sleep(10000);
			} catch (TwitterException e) {
				LOG.debug(e.getMessage());
			} catch (InterruptedException e) {
				LOG.debug(e.getMessage());
			}
		}
	}

	private static void run(String[] args) throws TwitterException {
		if (args.length == 0) {
			LOG.error("Twiktor needs at least one userId as argument");
			System.exit(0);
		}
		
		long usedUserId = getUserId(args);
		LOG.info("User Id to process: '{}'", usedUserId);
		Status status = getTwit(usedUserId);
		String statusText = getText(status);
		LOG.info("Used status text is '{}'", statusText);
		
		String newText = TRANSFORMER.tranform(statusText);
		if(newText.length() > 140) {
			newText.substring(0, 140);
		}
		LOG.info("New text is '{}'", newText);
		

		long inReply = status.getId();
		String userNameToReply = getUserName(status);
		StatusUpdate update = new StatusUpdate("@" + userNameToReply + " " + newText);
		update.setInReplyToStatusId(inReply);
		twitter.updateStatus(update);
		
		Status newStatus = twitter.updateStatus(newText);
		LOG.info("Status updated. Id is '{}'", newStatus.getId());
	}

	private static String getUserName(Status status) {
		if(!status.isRetweet()) {
			return status.getUser().getScreenName();
		}
		return status.getUserMentionEntities()[0].getScreenName();
	}

	private static String getText(Status status) {
		if(!status.isRetweet()) {
			return status.getText();
		}
		int columnPos = status.getText().indexOf(':');
		return status.getText().substring(columnPos + 1);
	}

	// TODO rewrite
	private static Status getTwit(long usedUserId) throws TwitterException {
		// get random status from last 1000
		Paging paging = new Paging(1, 1000);
		List<Status> statusList = twitter.getUserTimeline(usedUserId, paging);
		Status status = statusList.get(ThreadLocalRandom.current().nextInt(statusList.size()));
		return status;
	}

	private static long getUserId(String[] args) throws TwitterException {
		int userIdx = ThreadLocalRandom.current().nextInt(args.length);
		long userId = twitter.showUser(args[userIdx]).getId();
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
