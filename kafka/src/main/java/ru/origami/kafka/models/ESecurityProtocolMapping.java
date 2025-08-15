package ru.origami.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.kafka.models.ESaslMechanism.*;
import static ru.origami.kafka.models.ESecurityProtocol.*;

@Getter
@AllArgsConstructor
public enum ESecurityProtocolMapping {

    PLAINTEXT(ESecurityProtocol.PLAINTEXT, null),

    SSL(ESecurityProtocol.SSL, null),
    SSL_EXTERNAL(ESecurityProtocol.SSL, EXTERNAL),

    SASL_PLAINTEXT_PLAIN(SASL_PLAINTEXT, PLAIN),
    SASL_PLAINTEXT_SCRAM_SHA_256(SASL_PLAINTEXT, SCRAM_SHA_256),
    SASL_PLAINTEXT_SCRAM_SHA_512(SASL_PLAINTEXT, SCRAM_SHA_512),
    SASL_PLAINTEXT_GSSAPI(SASL_PLAINTEXT, GSSAPI),
    SASL_PLAINTEXT_OAUTHBEARER(SASL_PLAINTEXT, OAUTHBEARER),

    SASL_SSL_PLAIN(SASL_SSL, PLAIN),
    SASL_SSL_SCRAM_SHA_256(SASL_SSL, SCRAM_SHA_256),
    SASL_SSL_SCRAM_SHA_512(SASL_SSL, SCRAM_SHA_512),
    SASL_SSL_GSSAPI(SASL_SSL, GSSAPI),
    SASL_SSL_OAUTHBEARER(SASL_SSL, OAUTHBEARER),
    SASL_SSL_EXTERNAL(SASL_SSL, EXTERNAL);

    private final ESecurityProtocol securityProtocol;

    private final ESaslMechanism saslMechanism;

    public static void checkMapping(ESecurityProtocol protocol, ESaslMechanism saslMechanism) {
        if (Arrays.stream(ESecurityProtocolMapping.values())
                .noneMatch(m -> m.getSecurityProtocol() == protocol && m.getSaslMechanism() == saslMechanism)) {
            fail(getLangValue("kafka.incorrect.security.protocol.mapping").formatted(protocol.name(),
                    Objects.isNull(saslMechanism) ? null : saslMechanism.getSaslMechanism()));
        }
    }
}
