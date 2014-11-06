package ru.spbau.twiktor;

public class TwitterRTFilter {
    private String textTwitter;

    public TwitterRTFilter(String newText) {
        if (newText == null || newText.isEmpty())
        {
            throw new NullPointerException("newText");
        }

        textTwitter = newText;
    }

    public String filter()
    {
        textTwitter = textTwitter.replaceAll("RT ?@[A-Za-z0-9]+:?", "");
        textTwitter = textTwitter.trim().replaceAll(" +", " ");

        return textTwitter;
    }
}
