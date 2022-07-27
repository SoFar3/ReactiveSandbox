package com.yteplyi.sandbox.reactive.payment_service.payments.repository.impl;

import com.yteplyi.sandbox.reactive.payment_service.payments.entity.PaymentMethod;
import com.yteplyi.sandbox.reactive.payment_service.payments.entity.TransactionEntity;
import com.yteplyi.sandbox.reactive.payment_service.payments.entity.TransactionStatus;
import com.yteplyi.sandbox.reactive.payment_service.payments.repository.TransactionRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String INSERT_TRANSACTION =
            "INSERT INTO transactions(customer_id, status, payment_method, start_timestamp)" +
                    " VALUES ($1, $2, $3, $4) RETURNING (id, customer_id, status, payment_method, start_timestamp, end_timestamp)";

    private Vertx vertx;

    private PgPool client;

    private Function<Row, TransactionEntity> TRANSACTION_MAPPER = row ->
            new TransactionEntity(
                    row.getLong(0),
                    row.getInteger(1),
                    PaymentMethod.valueOf(row.getString(2)),
                    TransactionStatus.valueOf(row.getString(3)),
                    row.getLocalDateTime(3),
                    null);


    public TransactionRepositoryImpl(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        configureDbClient(config);
    }

    private void configureDbClient(JsonObject config) {
        JsonObject dbConfig = config.getJsonObject("db");

        System.out.println(dbConfig.encodePrettily());

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(dbConfig.getInteger("port"))
                .setHost(dbConfig.getString("host"))
                .setDatabase(dbConfig.getString("database"))
                .setUser(dbConfig.getString("user"))
                .setPassword(dbConfig.getString("password"));

        JsonObject poolConfig = dbConfig.getJsonObject("connectionPool");
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(poolConfig.getInteger("maxSize"));

        this.client = PgPool.pool(vertx, connectOptions, poolOptions);
    }

    @Override
    public void save(TransactionEntity transaction, Handler<AsyncResult<TransactionEntity>> resultHandler) {
        Tuple transactionTuple = Tuple.of(
                transaction.getCustomerId(),
                transaction.getStatus(),
                transaction.getPaymentMethod(),
                LocalDateTime.now()
        );

        client.preparedQuery(INSERT_TRANSACTION)
                .execute(transactionTuple)
                .map(rowSet -> rowSet.rowCount() > 1 ? Future.<RowSet<Row>>failedFuture("Only one transaction should have been saved") : Future.succeededFuture(rowSet))
                .flatMap(Function.identity())
                .map(RowSet::iterator)
                .map(iterator -> iterator.hasNext() ? TRANSACTION_MAPPER.apply(iterator.next()) : null)
                .map(Optional::ofNullable)
                .onSuccess(
                        // TODO: Replace with nicer solution
                        maybeTx ->
                                maybeTx.map(tx -> {
                                            resultHandler.handle(Future.succeededFuture(tx));
                                            return Optional.empty();
                                        })
                                        .orElseGet(() -> {
                                            resultHandler.handle(Future.failedFuture("An error occurred during save process"));
                                            return Optional.empty();
                                        }))
                .onFailure(t -> resultHandler.handle(Future.failedFuture(t)));
    }

}
