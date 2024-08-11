package com.mikhailkarpov.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class Transaction implements Serializable {

  private static final long serialVersionUID = 1L;

  private final long id;
  private final long customerId;
  private final BigDecimal amount;
  private final Instant createdAt;

  public Transaction(long id, long customerId, BigDecimal amount, Instant createdAt) {
    this.id = id;
    this.customerId = customerId;
    this.amount = amount;
    this.createdAt = createdAt;
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
