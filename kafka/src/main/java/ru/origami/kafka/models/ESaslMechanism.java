package ru.origami.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.security.scram.ScramLoginModule;

@Getter
@AllArgsConstructor
public enum ESaslMechanism {

    PLAIN("PLAIN", "{PlainLoginModule} required username=\"%s\" password=\"%s\";"
            .replaceAll("\\{PlainLoginModule}", PlainLoginModule.class.getName()), false),
    SCRAM_SHA_256("SCRAM-SHA-256", "{ScramLoginModule} required username=\"%s\" password=\"%s\";"
            .replaceAll("\\{ScramLoginModule}", ScramLoginModule.class.getName()), false),
    SCRAM_SHA_512("SCRAM-SHA-512", "{ScramLoginModule} required username=\"%s\" password=\"%s\";"
            .replaceAll("\\{ScramLoginModule}", ScramLoginModule.class.getName()), false),
    GSSAPI("GSSAPI", null, true),
    OAUTHBEARER("OAUTHBEARER", "{OAuthBearerLoginModule} required clientId=\"%s\" clientSecret=\"%s\";"
            .replaceAll("\\{OAuthBearerLoginModule}", OAuthBearerLoginModule.class.getName()), false),
    EXTERNAL("EXTERNAL", null, false);

    private final String saslMechanism;

    private final String saslJaasConfig;

    private final boolean needSaslKerberosServiceName;
}
