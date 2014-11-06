package ru.spbau.twiktor.transform;

import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Трансформирует твит в похожий семантически
 *
 * @author Sergey Tselovalnikov
 * @since 11/5/14
 */
public interface TwitTransformer {
    String tranform(String statusText);
    String tranform(Status status, Twitter twitter);
}
