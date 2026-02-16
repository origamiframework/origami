package ru.origami.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.oauthbearer.secured.OAuthBearerLoginCallbackHandler;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.origami.kafka.models.Properties;

import java.util.Objects;
import java.util.UUID;

import static ru.origami.kafka.models.ESaslMechanism.OAUTHBEARER;

public class Connection {

    private Connection() {
    }

    private static Producer<String, String> initProducer(Properties properties) {
        try {
            final java.util.Properties props = new java.util.Properties();

            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, properties.getSecurityProtocol());
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);

            buildProps(props, properties);

            return new KafkaProducer<>(props);
        } catch (Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    private static Consumer<String, String> initConsumer(Properties properties, boolean isEarliest) {
        try {
            final java.util.Properties props = new java.util.Properties();

            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, isEarliest ? "earliest" : "latest");
//            props.put("enable.partition.eof", "false");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//            props.put("consumer.timeout.ms", "3000"); для старых версий consumer
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, properties.getSecurityProtocol());
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.MAX_VALUE);
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 15 * 60 * 1000);
            props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, Integer.MAX_VALUE);
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, Objects.isNull(properties.getGroupId())
                    ? UUID.randomUUID().toString()
                    : properties.getGroupId());

            buildProps(props, properties);

            return new KafkaConsumer<>(props);
        } catch (Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    private static void buildProps(java.util.Properties props, Properties properties) {
        if (Objects.nonNull(properties.getSaslJaasConfig())) {
            props.put(SaslConfigs.SASL_JAAS_CONFIG, properties.getSaslJaasConfig());
        }

        if (Objects.nonNull(properties.getSaslMechanism())) {
            props.put(SaslConfigs.SASL_MECHANISM, properties.getSaslMechanism().getSaslMechanism());

            if (properties.getSaslMechanism().equals(OAUTHBEARER)) {
                props.put(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, OAuthBearerLoginCallbackHandler.class.getName());
            }
        }

        // SslTruststore
        if (Objects.nonNull(properties.getSslTruststoreLocation())) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, properties.getSslTruststoreLocation());
        }

        if (Objects.nonNull(properties.getSslTruststorePassword())) {
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, properties.getSslTruststorePassword());
        }

        // SaslOauthBearer
        if (Objects.nonNull(properties.getSaslOauthBearerTokenEndpoint())) {
            props.put(SaslConfigs.SASL_OAUTHBEARER_TOKEN_ENDPOINT_URL, properties.getSaslOauthBearerTokenEndpoint());
        }

        if (Objects.nonNull(properties.getSaslOauthBearerJwksEndpoint())) {
            props.put(SaslConfigs.SASL_OAUTHBEARER_JWKS_ENDPOINT_URL, properties.getSaslOauthBearerJwksEndpoint());
        }

        // SslKeystore
        if (Objects.nonNull(properties.getSslKeystoreLocation())) {
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, properties.getSslKeystoreLocation());
        }

        if (Objects.nonNull(properties.getSslKeystorePassword())) {
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, properties.getSslKeystorePassword());
        }

        if (Objects.nonNull(properties.getSslKeyPassword())) {
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, properties.getSslKeyPassword());
        }

        // SaslKerberos. Только при GSSAPI
        if (Objects.nonNull(properties.getSaslKerberosServiceName())) {
            props.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, properties.getSaslKerberosServiceName());
        }

//            if (Objects.nonNull(properties.getSaslMechanism()) && properties.getSaslMechanism().equals(OAUTHBEARER)) {
//                props.put(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG, InsecureSslEngineFactory.class);
//                props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/usr/lib/jvm/bellsoft-java8-full-amd64/jre/lib/security/cacerts");
//                props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "changeit");
//            }
    }

    static Producer<String, String> getProducer(Properties properties) {
        return initProducer(properties);
    }

    static Consumer<String, String> getConsumer(Properties properties, boolean isEarliest) {
        return initConsumer(properties, isEarliest);
    }
}
