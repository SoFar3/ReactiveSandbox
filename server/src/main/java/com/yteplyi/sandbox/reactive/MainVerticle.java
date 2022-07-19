package com.yteplyi.sandbox.reactive;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(new WebVerticle());
        vertx.deployVerticle(new NotificationConsumerVerticle());
    }

}
