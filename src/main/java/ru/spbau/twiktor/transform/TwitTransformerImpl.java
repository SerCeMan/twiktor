package ru.spbau.twiktor.transform;

import twitter4j.Status;

public class TwitTransformerImpl implements TwitTransformer {
    private final TransformerFactory factory = new TransformerFactory();

    @Override
    public String tranform(String twit) {
        TwitTransformer transformer = factory.getTransformer("simple");
        return transformer.tranform(twit);
    }

	@Override
	public String tranform(Status status) {
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
