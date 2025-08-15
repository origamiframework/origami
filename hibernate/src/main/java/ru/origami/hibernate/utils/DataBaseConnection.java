package ru.origami.hibernate.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.origami.hibernate.models.DataBaseSessionProperties;

public class DataBaseConnection {

    private DataBaseConnection() {
    }

    private static SessionFactory initSessionFactory(DataBaseSessionProperties sessionProperties) {
        try {
            final Configuration configuration = new Configuration()
                    .setProperty("hibernate.connection.url", sessionProperties.getConnectionUrl())
                    .setProperty("hibernate.connection.username", sessionProperties.getDbUserName())
                    .setProperty("hibernate.connection.password", sessionProperties.getDbPassword())
                    .configure(sessionProperties.getResource());

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
