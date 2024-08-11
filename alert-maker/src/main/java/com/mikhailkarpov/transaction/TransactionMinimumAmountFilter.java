package com.mikhailkarpov.transaction;

import java.math.BigDecimal;
import org.apache.flink.api.common.functions.FilterFunction;

public class TransactionMinimumAmountFilter implements FilterFunction<Transaction> {

  private final BigDecimal minAmount;

  public TransactionMinimumAmountFilter(BigDecimal minAmount) {
    this.minAmount = minAmount;
  }

  @Override
  public boolean filter(Transaction value) {
    return value.getAmount().compareTo(minAmount) >= 0;
  }

}
