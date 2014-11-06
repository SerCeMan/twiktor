package ru.spbau.twiktor.transform;

import twitter4j.Status;
import twitter4j.Twitter;

public class SimpleTransformer implements TwitTransformer {
    @Override
    public String tranform(String twit) {
        return twit + " Я щитаю";
    }

	@Override
	public String tranform(Status status, Twitter twitter, String tag) {
		return tranform(getText(status));
	}
	
	private String getText(Status status) {
		if(!status.isRetweet()) {
			return status.getText();
		}
		int columnPos = status.getText().indexOf(':');
		return status.getText().substring(columnPos + 1);
	}
}
