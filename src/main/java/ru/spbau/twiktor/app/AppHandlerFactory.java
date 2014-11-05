package ru.spbau.twiktor.app;

import ratpack.groovy.Groovy;
import ratpack.groovy.templating.TemplatingModule;
import ratpack.guice.Guice;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.launch.HandlerFactory;
import ratpack.launch.LaunchConfig;
import ratpack.launch.RatpackMain;
import ratpack.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика хэндлеров
 *
 * @author Sergey Tselovalnikov
 * @since 05.11.14
 */
public class AppHandlerFactory implements HandlerFactory {

    public static void main(String[] args) throws Exception {
        new RatpackMain().start();
    }

    @Override
    public Handler create(LaunchConfig launchConfig) throws Exception {
        return Guice.builder(launchConfig)
                .bindings(bindings -> bindings.add(
                        TemplatingModule.class
                ))
                .build(chain -> chain
                                .handler("", context -> {
                                    BotHandler botHandler = chain.getRegistry().get(BotHandler.class);
                                    Map<String, Object> params = new HashMap<>();
                                    params.put("botHandler", botHandler);
                                    context.render(Groovy.groovyTemplate(params, "index.html"));
                                })
                                .post("add-bot", context -> {
                                    BotHandler botHandler = chain.getRegistry().get(BotHandler.class);
                                    MultiValueMap<String, String> params = context.getRequest().getQueryParams();
                                    String login = params.get("login");
                                    String password = params.get("login");

                                    botHandler.addBot(login, password);

                                    ok(context);
                                })
                                .post("run", context -> {
                                    BotHandler botHandler = chain.getRegistry().get(BotHandler.class);
                                    Integer id = extractId(context);
                                    botHandler.startBot(id);

                                    ok(context);
                                })
                                .post("stop", context -> {
                                    BotHandler botHandler = chain.getRegistry().get(BotHandler.class);
                                    Integer id = extractId(context);
                                    botHandler.stopBot(id);

                                    ok(context);
                                })
                                .post("send-all", context -> {
                                    BotHandler botHandler = chain.getRegistry().get(BotHandler.class);
                                    MultiValueMap<String, String> params = context.getRequest().getQueryParams();
                                    botHandler.sendToAll(params.get("message"));

                                    ok(context);
                                })
                                .prefix("static", nested -> nested.assets("assets"))
                );
    }

    private static void ok(Context context) {
        context.getResponse().send("OK");
    }


    private static Integer extractId(Context context) {
        MultiValueMap<String, String> params = context.getRequest().getQueryParams();
        return Integer.valueOf(params.get("id"));
    }
}
