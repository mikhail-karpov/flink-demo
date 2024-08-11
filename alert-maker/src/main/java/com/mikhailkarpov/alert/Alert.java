package com.mikhailkarpov.alert;

import com.mikhailkarpov.transaction.Transaction;
import java.io.Serializable;
import java.time.Instant;

public class Alert implements Serializable {

  private static final long serialVersionUID = 1L;

  private final Transaction transaction;
  private final Instant createdAt;
  private final String reason;

  public Alert(Transaction transaction, String reason) {
    this.transaction = transaction;
    this.createdAt = Instant.now();
    this.reason = reason;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public String toString() {
    return "Alert{" +
        "transaction=" + transaction +
        ", createdAt=" + createdAt +
        ", reason='" + reason + '\'' +
        '}';
  }
}
