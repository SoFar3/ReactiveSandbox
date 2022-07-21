package com.yteplyi.sandbox.reactive.payment_service;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.getOrCreateContext()
                .runOnContext(v -> System.out.println("Payment service"));
    }

}
