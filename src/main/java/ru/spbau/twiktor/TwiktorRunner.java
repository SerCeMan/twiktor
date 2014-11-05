package ru.spbau.twiktor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spbau.twiktor.transform.TwitTransformerImpl;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class TwiktorRunner {
	private final static Logger LOG = LoggerFactory.getLogger(TwiktorRunner.class);
	
	public static void main(String[] args) throws IllegalStateException, TwitterException {
		if (args.length == 0) {
			LOG.error("Twiktor needs at least one user name as argument");
			System.exit(0);
		}
//		Twiktor twiktor = new Twiktor(new TwitTransformerImpl(), args, loadAccessToken());
	}

	private static AccessToken loadAccessToken() {
		String token = "2862320699-yn8rZdX4g4wWFwnMm4BLdVgZ91kT8iAAiCLtYJB";
		String tokenSecret = "Y57QmqGqfh4pjkwynKIrLwcycXKNWxSDoXpom4HcvzAJ7";
		return new AccessToken(token, tokenSecret);
	}
}
