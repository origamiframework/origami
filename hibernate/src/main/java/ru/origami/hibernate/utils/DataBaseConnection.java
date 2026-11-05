package ru.origami.hibernate.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.origami.hibernate.models.DataBaseSessionProperties;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;

public class DataBaseConnection {

    private DataBaseConnection() {
    }

    private static SessionFactory initSessionFactory(DataBaseSessionProperties sessionProperties) {
        try {
            String resource = sessionProperties.getResource();
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            URL configUrl = contextClassLoader == null
                    ? null
                    : contextClassLoader.getResource(resource);

            if (configUrl == null) {
                configUrl = DataBaseConnection.class
                        .getClassLoader()
                        .getResource(resource);
            }

            if (configUrl == null) {
                fail("Hibernate configuration resource not found: " + resource);
            }

            final Configuration configuration = new Configuration()
                    .setProperty("hibernate.connection.url", sessionProperties.getConnectionUrl())
                    .setProperty("hibernate.connection.username", sessionProperties.getDbUserName())
                    .setProperty("hibernate.connection.password", sessionProperties.getDbPassword())
                    .configure(configUrl);

            return configuration.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    public static SessionFactory getSessionFactory(DataBaseSessionProperties sessionProperties) {
        return initSessionFactory(sessionProperties);
    }
}
