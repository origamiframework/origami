package ru.origami.hibernate.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EHibernateResource {

    POSTGRES("hibernate-postgres.cfg.xml", "jdbc:log4jdbc:postgresql://%s:%s/%s?prepareThreshold=0"),
    CLICKHOUSE("hibernate-clickhouse.cfg.xml", "jdbc:log4jdbc:clickhouse://%s:%s/%s"),
    ORACLE("hibernate-oracle.cfg.xml", """
        jdbc:log4jdbc:oracle:thin:@(DESCRIPTION =
        (ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = %s)(PORT = %s)))
        (CONNECT_DATA = (SERVICE_NAME = %s)(SERVER = DEDICATED))
        (security=(ssl_server_cert_dn="CN=adb.oraclecloud.com,OU=Oracle US,O=Oracle Corporation,L=Redwood City,ST=California,C=US")))"""),
    MS_SQL("hibernate-mssql.cfg.xml", "jdbc:log4jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true");

    private String resource;

    private String connectionString;

    @Override
    public String toString() {
        return String.format("%s (%s)", resource, connectionString);
    }
}
