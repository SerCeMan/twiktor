package ru.spbau.twiktor.transform;

import twitter4j.Status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TwitTransformerSynonymizationImpl implements TwitTransformer  {

    private final String synonymDictionaryPath = "./src/main/resources/baza.txt";
    private final Map<String, List<String>> synonymDictionary = new HashMap<>();

    public TwitTransformerSynonymizationImpl()
    {
        Path original = Paths.get(synonymDictionaryPath);

        List<String> lines= null;
        try {
            lines = Files.readAllLines(original, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lines != null) {
            for(String line : lines){
                String[] data = line.split("\\|");

                for (int j = 0; j < data.length; ++j)
                {
                    String key = data[j];

                    List<String> words = new ArrayList<>();
                    for (int i = 0; i < data.length; ++i) {
                        if (i == j)
                        {
                            continue;
                        }

                        if (data[i] != null && !data[i].isEmpty()) {
                            words.add(data[i]);
                        }
                    }
                    synonymDictionary.put(key, words);
                }
            }
        }
    }

    @Override
    public String tranform(String twit) {
        if (twit == null)
        {
            throw new NullPointerException("twit");
        }

        String copyOfTwit = twit;

        copyOfTwit = twit.replaceAll("[^ йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮa-zA-Z0-9_-]", "");
        String[] words = copyOfTwit.split("\\s+");

        Random random = new Random();

        for (int i = 0; i < words.length; ++i)
        {
            if (words[i] != null && !words[i].isEmpty())
            {
                List<String> synonyms = synonymDictionary.get(words[i]);
                if (synonyms != null) {
                    int idx = random.nextInt(synonyms.size());
                    String randomSynonym = (synonyms.get(idx));

                    twit = twit.replace(words[i], randomSynonym);
                    int a = 5;
                }
            }
        }

        return twit;
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
