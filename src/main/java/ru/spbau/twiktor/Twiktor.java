package ru.spbau.twiktor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spbau.twiktor.transform.TwitTransformer;
import ru.spbau.twiktor.transform.TwitTransformerSynonymizationImpl;
import ru.spbau.twiktor.utils.ThreadUtils;
import twitter4j.*;
import twitter4j.Query.ResultType;
import twitter4j.auth.AccessToken;

public class Twiktor {
	private final static Logger LOG = LoggerFactory.getLogger(Twiktor.class);
    private final static AtomicInteger counter = new AtomicInteger();
	public static final int TwitterMaxLength = 140;
	private final TwitTransformer transformer;
	private final Twitter twitter;
	private String[] tags;
	private Timer timer;
	private volatile boolean isRunning = false;
    private final int id;
    private final String login;
    private final int followersCount;

    private static final Map<Long, Boolean> cache = new LinkedHashMap<Long, Boolean>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
            return size() > 1000;
        }
    };


    public Twiktor(String login, TwitTransformer transformer, String[] tags, AccessToken accessToken) throws IllegalStateException, TwitterException {
        this.login = login;
        id = counter.incrementAndGet();
		this.transformer = transformer;
		this.tags = tags;
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
        TwitTransformer synonimTransofrmer = new TwitTransformerSynonymizationImpl();
		Status newStatus = twitter.updateStatus(synonimTransofrmer.tranform(text));
		LOG.info("Status updated. Id is '{}'", newStatus.getId());
	}

	public Trends getTrends(int woeid)
	{
		Trends trends = null;
		try {
			trends = twitter.getPlaceTrends(woeid);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return trends;
	}

	private static Twitter getTwitter(AccessToken accessToken) {
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		// TODO load secret
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
	}

    public int getId() {
        return id;
    }
    
    public void setTags(String[] tags) {
    	this.tags = tags;
    }

    public void follow(String login) {
        try {
            twitter.createFriendship(login);
        } catch (TwitterException e) {
            LOG.error("Can not to follow " + login);
        }
    }

    private class PostStatusTask extends TimerTask {
		
		@Override
		public void run() {
			try {
                ThreadUtils.sleep(0, 5000); // случайная пауза от 0 до 5с
				String tag = tags[ThreadLocalRandom.current().nextInt(tags.length)];
				LOG.info("Tag to process: '{}'", tag);
				Status status = getTwit(tag);
				LOG.info("Used status text is '{}'", status.getText());
				
				String newText = transformer.tranform(status, twitter, tag);
                TwitTransformer synonymization = new TwitTransformerSynonymizationImpl();
                if(ThreadLocalRandom.current().nextInt(4) == 0) {
                    // повысил качество на ночь
                    newText = synonymization.tranform(newText);
                }
				if(newText == null) {
					return;
				}
				if(newText.length() > TwitterMaxLength) {
					newText = newText.substring(0, TwitterMaxLength);
				}
				LOG.info("New text is '{}'", newText);

                boolean reply = ThreadLocalRandom.current().nextBoolean();

                if(needToFollow()) {
                    twitter.createFriendship(status.getUser().getId());
                }

                if(ThreadLocalRandom.current().nextInt(5) == 0) {
                    twitter.createFavorite(status.getId());
                }

                // сделал ответы чуть чаще
                if(reply || ThreadLocalRandom.current().nextInt(4) == 1) {
                    replyTwit(status, newText);
                } else {
                    Status newStatus = twitter.updateStatus(newText);
                    LOG.info("Status updated. Id is '{}'", newStatus.getId());
                }
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}

        private void replyTwit(Status status, String newText) throws TwitterException {
			TwitterRTFilter twitterRTFilter = new TwitterRTFilter(newText);
			newText = twitterRTFilter.filter();

            long inReply = status.getId();
            String userNameToReply = getUserName(status);

			String fullMessage = "@" + userNameToReply + " " + newText;
			if(fullMessage.length() > TwitterMaxLength) {
				fullMessage = fullMessage.substring(0, TwitterMaxLength);
			}

			StatusUpdate update = new StatusUpdate(fullMessage);
            update.setInReplyToStatusId(inReply);
            twitter.updateStatus(update);
        }

        private String getUserName(Status status) {
			if(!status.isRetweet()) {
				return status.getUser().getScreenName();
			}
			return status.getUserMentionEntities()[0].getScreenName();
		}

		private Status getTwit(String tag) throws TwitterException {
			List<Status> statusList = twitter.search(new Query(tag).count(100).resultType(ResultType.recent)).getTweets();
			Status status = statusList.get(ThreadLocalRandom.current().nextInt(statusList.size()));

            int tryCount = 0;
            while (cache.containsKey(status.getId())) {
                tryCount++;
                status = statusList.get(ThreadLocalRandom.current().nextInt(statusList.size()));
                if(tryCount > 10) {
                    throw new RuntimeException("Error, too many repeated tweets with tag " + (tag));
                }
            }
            cache.put(status.getId(), Boolean.TRUE);

			return status;
		}
	}

    public AccessToken getAccessToken() {
        try {
            return twitter.getOAuthAccessToken();
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean needToFollow() {
        return ThreadLocalRandom.current().nextInt(10) == 0;
    }
}
