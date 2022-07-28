package com.yteplyi.sandbox.reactive.user_encouragement_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yteplyi.sandbox.reactive.user_encouragement_service.api.rest.dto.ServiceVersion;
import com.yteplyi.sandbox.reactive.user_encouragement_service.api.rest.dto.ServiceVersionResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouteNamingStrategy;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.openapi.RouterBuilderOptions;

import java.time.OffsetDateTime;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        ObjectMapper objectMapper = DatabindCodec.mapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        RouterBuilder
                .create(vertx, OpenapiSpec.USER_ENCOURAGEMENT_SERVICE_OPENAPI_SPEC)
                .compose(this::configureRouterBuilder)
                .compose(rb -> vertx.createHttpServer()
                        .requestHandler(rb.createRouter())
                        .listen(8080)
                        .onSuccess(v -> startPromise.complete()))
                .onFailure(Throwable::printStackTrace);
    }

    private Future<RouterBuilder> configureRouterBuilder(RouterBuilder routerBuilder) {
        RouterBuilderOptions routerBuilderOptions = new RouterBuilderOptions()
                .setRouteNamingStrategy(RouteNamingStrategy.OPERATION_OPENAPI_PATH);

        Promise<RouterBuilder> promise = Promise.promise();

        routerBuilder
                .setOptions(routerBuilderOptions)
                .operation("version")
                .handler(this::versionRoute);

        promise.complete(routerBuilder);

        return promise.future();
    }

    private void versionRoute(RoutingContext ctx) {
        ServiceVersion version = new ServiceVersion()
                .version("0.1.0");

        ServiceVersionResponse responseServiceVersion = new ServiceVersionResponse()
                .resource(ctx.request().path())
                .timestamp(OffsetDateTime.now())
                .data(version);

        ctx.end(JsonObject.mapFrom(responseServiceVersion).encode());
    }

}
