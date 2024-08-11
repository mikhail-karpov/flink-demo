package com.mikhailkarpov;

import com.mikhailkarpov.alert.Alert;
import com.mikhailkarpov.transaction.Transaction;
import com.mikhailkarpov.transaction.TransactionGenerator;
import com.mikhailkarpov.transaction.TransactionMinimumAmountFilter;
import java.math.BigDecimal;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.function.SerializableSupplier;

public class AlertMaker {

  public static void main(String[] args) throws Exception {
    SourceFunction<Transaction> transactionSource = new TransactionGenerator(10L, 100L);
    FilterFunction<Transaction> filter = new TransactionMinimumAmountFilter(new BigDecimal("500"));
    SerializableSupplier<ObjectMapper> objectMapperSupplier = buildObjectMapperSupplier();
    Sink<Alert> alertSink = buildAlertSink(objectMapperSupplier);

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.addSource(transactionSource)
        .filter(filter)
        .map(transaction -> new Alert(transaction, "Anomalous amount"))
        .sinkTo(alertSink);
    env.execute("Alert maker");
  }

  private static SerializableSupplier<ObjectMapper> buildObjectMapperSupplier() {
    return () -> new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(Include.NON_NULL);
  }

  private static KafkaSink<Alert> buildAlertSink(SerializableSupplier<ObjectMapper> objectMapperSupplier) {
    return KafkaSink.<Alert>builder()
        .setBootstrapServers("kafka:29092")
        .setRecordSerializer(KafkaRecordSerializationSchema.<Alert>builder()
            .setTopic("alerts")
            .setValueSerializationSchema(new JsonSerializationSchema<>(objectMapperSupplier))
            .build())
        .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
        .build();
  }

}
