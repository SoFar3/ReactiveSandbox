package com.yteplyi.sandbox.reactive.payment_service.payments.repository;

import com.yteplyi.sandbox.reactive.payment_service.payments.entity.TransactionEntity;
import com.yteplyi.sandbox.reactive.payment_service.payments.repository.impl.TransactionRepositoryImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface TransactionRepository {

    String ADDRESS = TransactionRepository.class.getName();

    static TransactionRepository create(Vertx vertx, JsonObject config) {
        return new TransactionRepositoryImpl(vertx, config);
    }

    static TransactionRepository createProxy(Vertx vertx, String address) {
        return new TransactionRepositoryVertxEBProxy(vertx, address);
    }

    void save(TransactionEntity transaction, Handler<AsyncResult<TransactionEntity>> resultHandler);

}
