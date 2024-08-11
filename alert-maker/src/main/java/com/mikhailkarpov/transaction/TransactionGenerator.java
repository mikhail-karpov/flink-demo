package com.mikhailkarpov.transaction;

import java.math.BigDecimal;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class TransactionGenerator implements SourceFunction<Transaction> {

  private final long customersSize;
  private final long sleepMillis;
  private volatile boolean isRunning;

  public TransactionGenerator(long customersSize, long sleepMillis) {
    if (customersSize <= 0) {
      throw new IllegalArgumentException("Customers size must be a positive number");
    }
    this.customersSize = customersSize;
    this.sleepMillis = sleepMillis;
  }

  @Override
  public void run(SourceContext<Transaction> ctx) {
    if (isRunning) {
      return;
    }

    isRunning = true;
    long nextTransactionId = 1L;

    for (long customerId = 1L; customerId <= customersSize; customerId++) {
      if (!isRunning) {
        break;
      }

      Transaction transaction = generateTransaction(nextTransactionId, customerId);
      ctx.collect(transaction);

      nextTransactionId++;
      if (nextTransactionId == Long.MAX_VALUE) {
        isRunning = false;
      }
      try {
        Thread.sleep(sleepMillis);
      } catch (InterruptedException e) {
        isRunning = false;
      }
    }
  }

  @Override
  public void cancel() {
    isRunning = false;
  }

  private static Transaction generateTransaction(long nextTransactionId, long customerId) {
    BigDecimal amount = Math.random() < 0.5 ? new BigDecimal("250.00") : new BigDecimal("750.00");
    Transaction transaction = new Transaction(nextTransactionId, customerId, amount);
    return transaction;
  }

}
