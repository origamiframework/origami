package ru.origami.hibernate.models;

import lombok.Getter;
import ru.origami.common.parallel.EnvironmentContext;

import java.util.Objects;

import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL;
import static ru.origami.common.environment.Environment.TEST_CONTAINERS_ENABLED;

@Getter
public class DataBaseSessionProperties {

    private EHibernateResource hibernateResource;
    private String connectionUrl;
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUserName;
    private String dbPassword;
    private String schema;
    private String defaultSchema;

    private DataBaseSessionProperties(Builder builder) {
        this.hibernateResource = builder.hibernateResource;
        this.connectionUrl = builder.connectionUrl;
        this.dbHost = builder.dbHost;
        this.dbPort = builder.dbPort;
        this.dbName = builder.dbName;
        this.dbUserName = builder.dbUserName;
        this.dbPassword = builder.dbPassword;
        this.schema = builder.schema;
        this.defaultSchema = builder.defaultSchema;
    }

    public class Builder {

        private EHibernateResource hibernateResource;
        private String connectionUrl;
        private String dbHost;
        private String dbPort;
        private String dbName;
        private String dbUserName;
        private String dbPassword;
        private String schema;
        private String defaultSchema;

        public Builder setHibernateResource(EHibernateResource hibernateResource) {
            this.hibernateResource = hibernateResource;

            return this;
        }

        public Builder setDbHost(String dbHost) {
            this.dbHost = dbHost;

            return this;
        }

        public Builder setDbPort(String dbPort) {
            this.dbPort = dbPort;

            return this;
        }

        public Builder setDbName(String dbName) {
            this.dbName = dbName;

            return this;
        }

        public Builder setDbUserName(String dbUserName) {
            this.dbUserName = dbUserName;

            return this;
        }

        public Builder setDbPassword(String dbPassword) {
            this.dbPassword = dbPassword;

            return this;
        }

        public Builder setSchema(String schema) {
            this.schema = getSchemaName(schema);

            return this;
        }

        public Builder setDefaultSchema(String defaultSchema) {
            this.defaultSchema = getSchemaName(defaultSchema);

            return this;
        }

        private String getSchemaName(String schema) {
            if (Objects.isNull(schema)) {
                return null;
            }

            if ("true".equalsIgnoreCase(TEST_CONTAINERS_ENABLED) && "true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
                return "%s_thread_%d;".formatted(schema, EnvironmentContext.getCurrent().getId());
            } else {
                return schema;
            }
        }

        public DataBaseSessionProperties build() {
            String connectionString = hibernateResource.getConnectionString();

            if (Objects.nonNull(defaultSchema)) {
                connectionString = new StringBuilder(connectionString)
                        .append(connectionString.contains("?") ? "&" : "?")
                        .append("currentSchema=")
                        .append(getSchemaName(defaultSchema))
                        .toString();
            }

            connectionUrl = String.format(connectionString, dbHost, dbPort, dbName);

            return new DataBaseSessionProperties(this);
        }
    }

    public String getConnectionString() {
        return hibernateResource.getConnectionString();
    }

    public String getResource() {
        return hibernateResource.getResource();
    }

    @Override
    public String toString() {
        return String.format("%s (%s:%s)", connectionUrl, dbUserName, dbPassword);
    }
}
