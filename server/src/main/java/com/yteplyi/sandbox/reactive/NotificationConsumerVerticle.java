package com.yteplyi.sandbox.reactive;

import io.vertx.core.AbstractVerticle;

public class NotificationConsumerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(Topic.NOTIFICATION_TOPIC.name, msg -> {
            String reply = "Notification with message: " + msg + " was consumed.";
            System.out.println(reply);
            msg.reply(reply);
        });
    }

}
