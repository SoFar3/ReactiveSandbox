package com.yteplyi.sandbox.reactive.payment_service.payments.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@AllArgsConstructor
@DataObject(generateConverter = true)
public class TransactionEntity implements Serializable {

    private Long id;

    private int customerId;

    private PaymentMethod paymentMethod;

    private TransactionStatus status;

    private LocalDateTime startTimestamp;

    private LocalDateTime endTimestamp;

    public TransactionEntity(JsonObject json) {
        this.id = json.getLong("id");
        this.customerId = json.getInteger("customerId");
        this.paymentMethod = PaymentMethod.valueOf(json.getString("paymentMethod"));
        this.status = TransactionStatus.valueOf(json.getString("status"));
        if (json.getInstant("startTimestamp") != null) {
            this.startTimestamp = LocalDateTime.ofInstant(json.getInstant("startTimestamp"), ZoneId.of("GMT"));
        }
        if (json.getInstant("endTimestamp") != null) {
            this.endTimestamp = LocalDateTime.ofInstant(json.getInstant("endTimestamp"), ZoneId.of("GMT"));
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        TransactionEntityConverter.toJson(this, json);
        return json;
    }

}
