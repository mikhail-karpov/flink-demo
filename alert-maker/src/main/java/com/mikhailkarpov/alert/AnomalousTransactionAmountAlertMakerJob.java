package com.mikhailkarpov.alert;

import com.mikhailkarpov.transaction.Transaction;
import com.mikhailkarpov.transaction.TransactionMinimumAmountFilter;
import java.math.BigDecimal;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class AnomalousTransactionAmountAlertMakerJob {

  private final Source<Transaction, ?, ?> transactionSource;
  private final Sink<Alert> alertSink;
  private final BigDecimal transactionAmountThreshold;

  public AnomalousTransactionAmountAlertMakerJob(
      Source<Transaction, ?, ?> transactionSource,
      Sink<Alert> alertSink,
      BigDecimal transactionAmountThreshold
  ) {
    this.transactionSource = transactionSource;
    this.alertSink = alertSink;
    this.transactionAmountThreshold = transactionAmountThreshold;
  }

  public JobExecutionResult execute() throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    env.fromSource(transactionSource, WatermarkStrategy.noWatermarks(), "Transactions source")
        .filter(new TransactionMinimumAmountFilter(transactionAmountThreshold))
        .map(transaction -> new Alert(transaction, "Anomalous amount"))
        .sinkTo(alertSink);

    return env.execute("Alert maker");
  }
}
