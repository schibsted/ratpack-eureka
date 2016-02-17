package com.schibsted.ratpack.eureka;

import ratpack.config.ConfigData;
import ratpack.guice.Guice;
import ratpack.health.HealthCheckHandler;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

public class ExampleApp {

    public static void main(String[] args) throws Exception {

        ServerConfig serverConfig = ServerConfig.embedded().baseDir(BaseDir.find()).build();
        ConfigData configData = ConfigData.of(c -> c.props(serverConfig.getBaseDir().file("application.properties")));

        EurekaFactory eurekaFactory = configData.get("/eureka", EurekaFactory.class);

        RatpackServer.start(spec -> spec
                .serverConfig(serverConfig)
                .registry(Guice.registry(r -> r
                        .bind(HealthCheckHandler.class)
                        .module(eurekaFactory.buildModule(serverConfig))))
                .handlers(chain -> chain
                        .path("hello", ctx -> {
                            ctx.render("hello to you too");
                        })
                        .path("health-check/:name?", HealthCheckHandler.class)));
    }
}
