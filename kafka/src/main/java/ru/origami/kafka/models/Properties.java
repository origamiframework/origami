package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.origami.common.environment.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Environment.STAND;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.kafka.models.ESaslMechanism.GSSAPI;
import static ru.origami.kafka.models.ESaslMechanism.OAUTHBEARER;

@Getter
@Slf4j
public class Properties {

    private String bootstrapServers;

    private String groupId;

    private String username;

    private String password;

    private String clientId;

    private String clientSecret;

    private String securityProtocol;

    private ESaslMechanism saslMechanism;

    private String saslJaasConfig;

    private String topicPrefix;

    private String topicPostfix;

    private String sslTruststoreLocation;

    private String sslTruststorePassword;

    private String saslOauthBearerTokenEndpoint;

    private String saslOauthBearerJwksEndpoint;

    private String saslKerberosServiceName;

    private String sslKeystoreLocation;

    private String sslKeystorePassword;

    private String sslKeyPassword;

    private Long retryWaitingTime;

    private Integer retryMaxAttempts;

    private Long retryReadTimeout;

    public Properties(Builder builder) {
        this.bootstrapServers = builder.bootstrapServers;
        this.groupId = builder.groupId;
        this.username = builder.username;
        this.password = builder.password;
        this.securityProtocol = builder.securityProtocol.name();
        this.saslMechanism = builder.saslMechanism;
        this.saslJaasConfig = builder.saslJaasConfig;
        this.topicPrefix = builder.topicPrefix;
        this.topicPostfix = builder.topicPostfix;
        this.sslTruststoreLocation = builder.sslTruststoreLocation;
        this.sslTruststorePassword = builder.sslTruststorePassword;
        this.saslOauthBearerTokenEndpoint = builder.saslOauthBearerTokenEndpoint;
        this.saslOauthBearerJwksEndpoint = builder.saslOauthBearerJwksEndpoint;
        this.saslKerberosServiceName = builder.saslKerberosServiceName;
        this.sslKeystoreLocation = builder.sslKeystoreLocation;
        this.sslKeystorePassword = builder.sslKeystorePassword;
        this.sslKeyPassword = builder.sslKeyPassword;
        this.retryMaxAttempts = builder.retryMaxAttempts;
        this.retryReadTimeout = builder.retryReadTimeout;
        this.retryWaitingTime = builder.retryWaitingTime;
    }

    public static class Builder {

        @Setter
        private String bootstrapServers;

        @Setter
        private String groupId;

        @Setter
        private String username;

        @Setter
        private String password;

        @Setter
        private String clientId;

        @Setter
        private String clientSecret;

        private ESecurityProtocol securityProtocol;

        private Map<String, ESecurityProtocol> securityProtocols = new HashMap<>();

        private ESaslMechanism saslMechanism;

        private Map<String, ESaslMechanism> saslMechanisms = new HashMap<>();

        private String saslJaasConfig;

        @Setter
        private String topicPrefix;

        @Setter
        private String topicPostfix;

        @Setter
        private String sslTruststoreLocation;

        @Setter
        private String sslTruststorePassword;

        @Setter
        private String saslOauthBearerTokenEndpoint;

        @Setter
        private String saslOauthBearerJwksEndpoint;

        @Setter
        private String saslKerberosServiceName;

        @Setter
        private String sslKeystoreLocation;

        @Setter
        private String sslKeystorePassword;

        @Setter
        private String sslKeyPassword;

        @Setter
        private Long retryWaitingTime;

        @Setter
        private Integer retryMaxAttempts;

        @Setter
        private Long retryReadTimeout;

        private final String ALL_STAND_REGEXP = ".*";

        public Builder addSecurityProtocol(ESecurityProtocol securityProtocol) {
            this.securityProtocols.put(ALL_STAND_REGEXP, securityProtocol);

            return this;
        }

        public Builder addSecurityProtocol(ESecurityProtocol securityProtocol, String standRegexp) {
            this.securityProtocols.put(standRegexp, securityProtocol);

            return this;
        }

        public Builder addSaslMechanism(ESaslMechanism saslMechanism) {
            this.saslMechanisms.put(ALL_STAND_REGEXP, saslMechanism);

            return this;
        }

        public Builder addSaslMechanism(ESaslMechanism saslMechanism, String standRegexp) {
            this.saslMechanisms.put(standRegexp, saslMechanism);

            return this;
        }

        private <T> T getValueForStand(Map<String, T> map) {
            String stand = Environment.get(STAND);

            for (Map.Entry<String, T> entry : map.entrySet()) {
                if (ALL_STAND_REGEXP.equals(entry.getKey())) {
                    continue;
                }

                if (Pattern.matches(entry.getKey(), stand)) {
                    return entry.getValue();
                }
            }

            return map.getOrDefault(ALL_STAND_REGEXP, null);
        }

        public Properties build() {
            securityProtocol = getValueForStand(securityProtocols);
            saslMechanism = getValueForStand(saslMechanisms);

            if (Objects.isNull(securityProtocol)) {
                fail(getLangValue("kafka.empty.security.protocol"));
            }

            ESecurityProtocolMapping.checkMapping(securityProtocol, saslMechanism);

            if (Objects.nonNull(this.saslMechanism) && Objects.nonNull(this.saslMechanism.getSaslJaasConfig())) {
                if (this.saslMechanism == OAUTHBEARER) {
                    if (Objects.isNull(clientId)) {
                        fail(getLangValue("kafka.empty.client.id"));
                    }

                    if (Objects.isNull(clientSecret)) {
                        fail(getLangValue("kafka.empty.client.secret"));
                    }

                    this.saslJaasConfig = this.saslMechanism.getSaslJaasConfig().formatted(clientId, clientSecret);

                    if (StringUtils.isBlank(saslOauthBearerTokenEndpoint)) {
                        log.info(getLangValue("kafka.empty.sasl.oauth.bearer.token.endpoint"));
                    }

                    if (StringUtils.isBlank(saslOauthBearerJwksEndpoint)) {
                        log.info(getLangValue("kafka.empty.sasl.oauth.bearer.jwks.endpoint"));
                    }
                } else {
                    if (Objects.isNull(username)) {
                        fail(getLangValue("kafka.empty.username"));
                    }

                    if (Objects.isNull(password)) {
                        fail(getLangValue("kafka.empty.password"));
                    }

                    this.saslJaasConfig = this.saslMechanism.getSaslJaasConfig().formatted(username, password);
                }
            }

            if (securityProtocol.isWithSslTruststore() && StringUtils.isBlank(sslTruststoreLocation)) {
                log.info(getLangValue("kafka.empty.ssl.truststore.location"));
            }

            if (securityProtocol.isWithSslTruststore() && StringUtils.isNotBlank(sslTruststoreLocation)
                    && StringUtils.isBlank(sslTruststorePassword)) {
                log.info(getLangValue("kafka.empty.ssl.truststore.password"));
            }

            if (StringUtils.isNotBlank(sslKeystoreLocation) && StringUtils.isBlank(sslKeystorePassword)) {
                log.info(getLangValue("kafka.empty.ssl.keystore.password"));
            }

            if (StringUtils.isNotBlank(sslKeystoreLocation) && StringUtils.isBlank(sslKeyPassword)) {
                log.info(getLangValue("kafka.empty.ssl.key.password"));
            }

            if (Objects.nonNull(saslMechanism) && saslMechanism.isNeedSaslKerberosServiceName() && Objects.isNull(saslKerberosServiceName)) {
                log.info(getLangValue("kafka.empty.sasl.kerberos.service.name"));
            }

            if (this.saslMechanism == GSSAPI) {
                // для инфо - необходимо задать внешний конфиг -Djava.security.auth.login.config=/path/to/jaas.conf
            }

            return new Properties(this);
        }
    }

    @Override
    public String toString() {
        String username = null;
        String password = null;

        if (Objects.nonNull(this.username)) {
            username = this.username;
            password = this.password;
        } else if (Objects.nonNull(clientId)) {
            username = clientId;
            password = clientSecret;
        }

        return String.format("%s:%s - %s", username, password, groupId);
    }
}
