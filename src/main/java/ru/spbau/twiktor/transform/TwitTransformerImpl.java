package ru.spbau.twiktor.transform;

public class TwitTransformerImpl implements TwitTransformer {
    private final TransformerFactory factory = new TransformerFactory();

    @Override
    public String tranform(String twit) {
        TwitTransformer transformer = factory.getTransformer("simple");
        return transformer.tranform(twit);
    }
}
