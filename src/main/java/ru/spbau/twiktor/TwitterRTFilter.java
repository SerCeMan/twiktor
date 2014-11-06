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
        if (textTwitter.startsWith("RT"))
        {
            textTwitter = textTwitter.replaceAll("^RT ?@[A-Za-z0-9]+:?", "");
            textTwitter = textTwitter.substring(1);
        }

        return textTwitter;
    }

    public static void main(String[] args) {
        String text = "RT @zizudyfyzesi: Путин высказался напротив попыток переписать историю";

        TwitterRTFilter twitterRTFilter = new TwitterRTFilter(text);

        System.out.print(twitterRTFilter.filter());
    }
}
