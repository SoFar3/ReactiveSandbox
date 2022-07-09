package com.yteplyi.sandbox.reactive;

import io.vertx.core.AbstractVerticle;

public class SimpleVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    req.response().end("Hello world from Vert.x");
                }).listen(8080);
    }

}
