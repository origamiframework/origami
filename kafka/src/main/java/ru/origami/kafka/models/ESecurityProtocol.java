package ru.origami.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ESecurityProtocol {

    PLAINTEXT(false),
    SASL_PLAINTEXT(false),
    SASL_SSL(true),
    SSL(true);

    private final boolean withSslTruststore;
}
