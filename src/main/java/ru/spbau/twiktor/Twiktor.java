package ru.spbau.twiktor;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spbau.twiktor.transform.TwitTransformer;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Twiktor {
	private final static Logger LOG = LoggerFactory.getLogger(Twiktor.class);
    private final static AtomicInteger counter = new AtomicInteger();
	private final TwitTransformer transformer;
	private final Twitter twitter;
	private final String[] accounts;
	private Timer timer;
	private volatile boolean isRunning = false;
    private final int id;
    private final String login;
    private final int followersCount;

    public Twiktor(String login, TwitTransformer transformer, String[] accounts, AccessToken accessToken) throws IllegalStateException, TwitterException {
        this.login = login;
        id = counter.incrementAndGet();
		this.transformer = transformer;
		this.accounts = accounts;
		twitter = getTwitter(accessToken);
		long authUserId = twitter.getId();
        followersCount = twitter.users().showUser(login).getFollowersCount();
        LOG.info("Twitter created. Used user id is '{}'", authUserId);
    }

    public int getFollowersCount() {
        return followersCount;
    }

	public boolean isRunning() {
		return isRunning;
	}

    public String getLogin() {
        return login;
    }

	public void start() {
		if(isRunning) {
			return;
		}
		timer = new Timer();
		timer.schedule(new PostStatusTask(), 0, 30000);
		isRunning = true;
	}
	
	public void stop() {
		if(!isRunning) {
			return;
		}
		timer.cancel();
		timer.purge();
		timer = null;
        isRunning = false;
	}
	
	public void postStatus(String text) throws TwitterException {
		Status newStatus = twitter.updateStatus(transformer.tranform(text));
		LOG.info("Status updated. Id is '{}'", newStatus.getId());
	}

	private static Twitter getTwitter(AccessToken accessToken) {
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		// TODO load secret
		twitter.setOAuthConsumer("ANV7A3SBGxsUz1z4LRETSfQCZ",
				"n82pOOT3vP9CJtRPrtm0qm7Y5DG2XXogteD1sQDezAGPGsI3bk");
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}

    public int getId() {
        return id;
    }

    private class PostStatusTask extends TimerTask {
		
		@Override
		public void run() {
			try {
				long usedUserId;
				usedUserId = getUserId(accounts);
				LOG.info("User Id to process: '{}'", usedUserId);
				Status status = getTwit(usedUserId);
				String statusText = getText(status);
				LOG.info("Used status text is '{}'", statusText);
				
				String newText = transformer.tranform(statusText);
				if(newText == null) {
					return;
				}
				if(newText.length() > 140) {
					newText = newText.substring(0, 140);
				}
				LOG.info("New text is '{}'", newText);
				
				long inReply = status.getId();
				String userNameToReply = getUserName(status);
				StatusUpdate update = new StatusUpdate("@" + userNameToReply + " " + newText);
				update.setInReplyToStatusId(inReply);
				twitter.updateStatus(update);
				
				Status newStatus = twitter.updateStatus(newText);
				LOG.info("Status updated. Id is '{}'", newStatus.getId());
			} catch (TwitterException e) {
				LOG.error(e.getMessage());
			}
		}
		
		private String getUserName(Status status) {
			if(!status.isRetweet()) {
				return status.getUser().getScreenName();
			}
			return status.getUserMentionEntities()[0].getScreenName();
		}

		private String getText(Status status) {
			if(!status.isRetweet()) {
				return status.getText();
			}
			int columnPos = status.getText().indexOf(':');
			return status.getText().substring(columnPos + 1);
		}

		private Status getTwit(long usedUserId) throws TwitterException {
			// get random status from last 1000
			Paging paging = new Paging(1, 1000);
			List<Status> statusList = twitter.getUserTimeline(usedUserId, paging);
			Status status = statusList.get(ThreadLocalRandom.current().nextInt(statusList.size()));
			return status;
		}

		private long getUserId(String[] args) throws TwitterException {
			int userIdx = ThreadLocalRandom.current().nextInt(args.length);
			long userId = twitter.showUser(args[userIdx]).getId();
			return userId;
		}

	}
}
