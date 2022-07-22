package com.yteplyi.sandbox.reactive.user_encouragement_service;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.openapi.RouteNamingStrategy;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.openapi.RouterBuilderOptions;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        RouterBuilder
                .create(vertx, Spec.USER_ENCOURAGEMENT_SERVICE_OPENAPI_SPEC)
                .onSuccess(this::configureRouterBuilder)
                .compose(rb -> vertx.createHttpServer()
                            .requestHandler(rb.createRouter())
                            .listen(8080));
    }

    private void configureRouterBuilder(RouterBuilder routerBuilder) {
        RouterBuilderOptions routerBuilderOptions = new RouterBuilderOptions()
                .setRouteNamingStrategy(RouteNamingStrategy.OPERATION_OPENAPI_PATH);
        routerBuilder
                .setOptions(routerBuilderOptions)
                .operation("version")
                .handler(ctx -> ctx.end("0.1.0"));
    }

}
