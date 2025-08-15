package ru.origami.kafka.models;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static ru.origami.common.environment.Language.getLangValue;

@Getter
public class KafkaRecord<T> {

    private String key;

    private LocalDateTime timestamp;

    private T value;

    private String topic;

    private Headers headers;

    private int partition;

    private long offset;

    public KafkaRecord(ConsumerRecord<String, T> record) {
        this.key = record.key();
        this.timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), TimeZone.getDefault().toZoneId());
        this.value = record.value();
        this.topic = record.topic();
        this.headers = record.headers();
        this.partition = record.partition();
        this.offset = record.offset();
    }

    public KafkaRecord(KafkaRecord<String> record, T value) {
        this.key = record.getKey();
        this.timestamp = record.getTimestamp();
        this.value = value;
        this.topic = record.getTopic();
        this.headers = record.getHeaders();
        this.partition = record.getPartition();
        this.offset = record.getOffset();
    }

    public String getFormattedRecord() {
        return getLangValue("kafka.formatted.record").formatted(key, timestamp, Objects.isNull(value)
                ? "null" : String.format("\n\t\t%s", value.toString().replaceAll("\n", "\n\t\t")));
    }

    public List<Header> getHeadersList() {
        return List.of(headers.toArray());
    }

    public List<Header> getHeadersByName(String name) {
        return getHeadersList().stream()
                .filter(h -> h.key().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("%s, %s: %s", key, timestamp, value);
    }
}
