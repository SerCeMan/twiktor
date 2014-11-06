package ru.spbau.twiktor.app;

import twitter4j.auth.RequestToken;

/**
 * Контейнер, который содержит все для OAuth. Используется для аутентификации
 *
 * @author Sergey Tselovalnikov
 * @since 06.11.14
 */
public class AuthContainer {
    public final String login;
    public final RequestToken requestToken;

    public AuthContainer(String login, RequestToken requestToken) {
        this.login = login;
        this.requestToken = requestToken;
    }

    public String getLogin() {
        return login;
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }
}
