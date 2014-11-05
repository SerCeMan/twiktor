package ru.spbau.twiktor.transform;

public class SimpleTransformer implements TwitTransformer {
    @Override
    public String tranform(String twit) {
        return twit + "Я щитаю";
    }
}
