package com.mikhailkarpov;

import com.mikhailkarpov.alert.Alert;
import com.mikhailkarpov.transaction.Transaction;
import com.mikhailkarpov.transaction.TransactionGenerator;
import com.mikhailkarpov.transaction.TransactionMinimumAmountFilter;
import java.math.BigDecimal;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class AlertMaker {

  public static void main(String[] args) throws Exception {
    SourceFunction<Transaction> transactionSource = new TransactionGenerator(10L, 100L);
    FilterFunction<Transaction> filter = new TransactionMinimumAmountFilter(new BigDecimal("500"));

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.addSource(transactionSource)
        .filter(filter)
        .map(transaction -> new Alert(transaction, "Anomalous amount"))
        .print();
    env.execute("Alert maker");
  }

}
