package com.yteplyi.sandbox.reactive.payment_service;

import com.yteplyi.sandbox.reactive.payment_service.payments.entity.TransactionEntity;
import com.yteplyi.sandbox.reactive.payment_service.payments.repository.TransactionRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RestVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.post("/api/rest/v1/transactions").handler(this::saveTransactionHandler);

        System.out.println("Starting RestVerticle...");

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080)
                .onSuccess(ignore -> { System.out.println("Started"); startPromise.complete(); })
                .onFailure(t -> {
                    t.printStackTrace();
                    startPromise.fail(t.getMessage());
                });
    }

    private void saveTransactionHandler(RoutingContext ctx) {
        TransactionRepository transactionRepository = TransactionRepository.createProxy(vertx, TransactionRepository.ADDRESS);

        ctx.request().body()
                .onSuccess(buf -> {
                    JsonObject transactionJson = new JsonObject(buf);
                    TransactionEntity transactionEntity = new TransactionEntity(transactionJson);
                    transactionRepository.save(transactionEntity, tx -> {
                        if (tx.succeeded()) {
                            TransactionEntity result = tx.result();
                            System.out.printf("Transaction[%s] was saved\n", result.toString());
                            ctx.response()
                                    .setStatusCode(200)
                                    .end(new JsonObject()
                                            .put("id", result.getId())
                                            .encode()
                                    );
                        } else {
                            tx.cause().printStackTrace();
                            ctx.response()
                                    .setStatusCode(500)
                                    .end();
                        }
                    });
                });
    }

}
