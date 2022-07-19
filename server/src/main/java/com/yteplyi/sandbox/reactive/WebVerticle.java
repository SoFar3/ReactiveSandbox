package com.yteplyi.sandbox.reactive;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class WebVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.get("/api/v1/notify/:message").handler(this::notify);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    private void notify(RoutingContext context) {
        String notification = context.pathParam("message");
        vertx.eventBus().request(
                Topic.NOTIFICATION_TOPIC.name,
                "Simple notification: " + context.pathParam("message"),
                reply -> {
                    context.end("Notification with message: " +  notification + " was accepted.");
                });
    }

}
