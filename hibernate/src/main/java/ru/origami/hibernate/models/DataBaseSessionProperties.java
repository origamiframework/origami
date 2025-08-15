package ru.origami.hibernate.models;

import lombok.Getter;

import java.util.Objects;

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

    public static class Builder {

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
            this.schema = schema;

            return this;
        }

        public Builder setDefaultSchema(String defaultSchema) {
            this.defaultSchema = defaultSchema;

            return this;
        }

        public DataBaseSessionProperties build() {
            String connectionString = hibernateResource.getConnectionString();

            if (Objects.nonNull(defaultSchema)) {
                connectionString = new StringBuilder(connectionString)
                        .append(connectionString.contains("?") ? "&" : "?")
                        .append("currentSchema=")
                        .append(defaultSchema)
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
