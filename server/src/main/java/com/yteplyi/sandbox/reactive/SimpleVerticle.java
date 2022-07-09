package com.yteplyi.sandbox.reactive;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SimpleVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.get("/api/v1/hello").handler(this::helloWorldRoute);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    private void helloWorldRoute(RoutingContext context) {
        context.end("Hello world from Vert.x router");
    }

}
