package ru.spbau.twiktor.transform;

import ru.spbau.twiktor.transform.tomita.TomitaTransformer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Фабрика трансформеров
 *
 * @author Sergey Tselovalnikov
 * @since 11/5/14
 */
public class TransformerFactory {
    private ConcurrentHashMap<String, TwitTransformer> transformers = new ConcurrentHashMap<>();

    TransformerFactory() {
        transformers.put("tomita", new TomitaTransformer());
        transformers.put("simple", new SimpleTransformer());
        transformers.put("synonymization", new TwitTransformerSynonymizationImpl());
    }

    public TwitTransformer getTransformer(String name) {
        return transformers.get(name);
    }
}
