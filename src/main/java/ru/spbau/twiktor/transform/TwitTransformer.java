package ru.spbau.twiktor.transform;

/**
 * Трансформирует твит в похожий семантически
 *
 * @author Sergey Tselovalnikov
 * @since 11/5/14
 */
public interface TwitTransformer {
    String tranform(String twit);
}
