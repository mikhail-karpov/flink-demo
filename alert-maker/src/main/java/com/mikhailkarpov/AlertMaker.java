package com.mikhailkarpov;

import com.mikhailkarpov.alert.Alert;
import com.mikhailkarpov.alert.AnomalousTransactionAmountAlertMakerJob;
import com.mikhailkarpov.transaction.Transaction;
import java.math.BigDecimal;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.util.function.SerializableSupplier;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

public class AlertMaker {

  public static void main(String[] args) throws Exception {
    SerializableSupplier<ObjectMapper> objectMapperSupplier = buildObjectMapperSupplier();
    KafkaSource<Transaction> transactionSource =buildTransactionSource(objectMapperSupplier);
    Sink<Alert> alertSink = buildAlertSink(objectMapperSupplier);

    AnomalousTransactionAmountAlertMakerJob job = new AnomalousTransactionAmountAlertMakerJob(
        transactionSource,
        alertSink,
        new BigDecimal("500")
    );
    job.execute();
  }

  private static SerializableSupplier<ObjectMapper> buildObjectMapperSupplier() {
    return () -> new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(Include.NON_NULL);
  }

  private static KafkaSource<Transaction> buildTransactionSource(
      SerializableSupplier<ObjectMapper> objectMapperSupplier
  ) {
    DeserializationSchema<Transaction> transactionDeserializer =
        new JsonDeserializationSchema<>(Transaction.class, objectMapperSupplier);

    return KafkaSource.<Transaction>builder()
        .setBootstrapServers("kafka:29092")
        .setGroupId("alert-maker")
        .setTopics("transactions")
        .setValueOnlyDeserializer(transactionDeserializer)
        .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
        .build();
  }

  private static KafkaSink<Alert> buildAlertSink(
      SerializableSupplier<ObjectMapper> objectMapperSupplier
  ) {
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
