package com.yteplyi.sandbox.reactive.payment_service;

import com.yteplyi.sandbox.reactive.payment_service.payments.repository.TransactionRepository;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        prepareConfig()
                .compose(config ->
                        vertx.deployVerticle(
                                        new RestVerticle(),
                                        new DeploymentOptions().setConfig(config))
                                .compose(ignore -> Future.succeededFuture(config), Future::failedFuture))
                .compose(config -> completeServerStartup(startPromise, config))
                .onFailure(t -> {
                    t.printStackTrace();
                    startPromise.fail(t.getMessage());
                });
    }

    private Future<Void> completeServerStartup(Promise<Void> startPromise, JsonObject config) {
        new ServiceBinder(vertx)
                .setAddress(TransactionRepository.ADDRESS)
                .register(TransactionRepository.class, TransactionRepository.create(vertx, config));
        System.out.println("TransactionRepository registered");
        startPromise.complete();
        return Future.succeededFuture();
    }

    private Future<JsonObject> prepareConfig() {
        System.out.println("Preparing config");
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(false)
                .setConfig(new JsonObject().put("path", "config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        System.out.println("Config prepared");
        return retriever.getConfig();
    }

}
