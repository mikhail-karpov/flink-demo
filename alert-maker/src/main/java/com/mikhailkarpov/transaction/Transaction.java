package com.mikhailkarpov.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction implements Serializable {

  private static final long serialVersionUID = 1L;

  private final long id;
  private final long customerId;
  private final BigDecimal amount;
  private final Instant createdAt;

  @JsonCreator
  public Transaction(
      @JsonProperty("id") long id,
      @JsonProperty("customerId") long customerId,
      @JsonProperty("amount") BigDecimal amount,
      @JsonProperty("createdAt") Instant createdAt
  ) {
    this.id = id;
    this.customerId = customerId;
    this.amount = amount;
    this.createdAt = createdAt != null ? createdAt : Instant.now();
  }

  public Transaction(long id, long customerId, BigDecimal amount) {
    this(id, customerId, amount, Instant.now());
  }

  public long getId() {
    return id;
  }

  public long getCustomerId() {
    return customerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "Transaction{" +
        "id=" + id +
        ", customerId=" + customerId +
        ", amount=" + amount +
        ", createdAt=" + createdAt +
        '}';
  }
}
