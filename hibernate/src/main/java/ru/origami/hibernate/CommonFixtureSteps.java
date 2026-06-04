package ru.origami.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.origami.hibernate.models.DataBaseSessionProperties;
import ru.origami.hibernate.utils.DataBaseConnection;
import ru.origami.hibernate.utils.SchemaInspector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public class CommonFixtureSteps {

    public static final String DYNAMIC_SCHEMA = "${dynamic_bd_schema}";

    protected DataBaseSessionProperties sessionProperties;

    protected DBSession session;

    private static Map<Thread, Map<DataBaseSessionProperties, DBSession>> dbSessions = new HashMap<>();

    protected void initSession() {
        Thread currentThread = Thread.currentThread();

        synchronized (dbSessions) {
            if (!dbSessions.containsKey(currentThread)) {
                dbSessions.put(currentThread, new HashMap<>());
            }

            if (!dbSessions.get(currentThread).containsKey(sessionProperties)) {
                dbSessions.get(currentThread).put(sessionProperties, openDataBaseConnection());
            }
        }

        session = dbSessions.get(currentThread).get(sessionProperties);
    }

    private DBSession openDataBaseConnection() {
        if (sessionProperties == null) {
            fail(getLangValue("hibernate.props.null"));
        }

        try {
            SessionFactory sessionFactory = DataBaseConnection.getSessionFactory(sessionProperties);
            Session currSession;
            String schema = null;

            if (Objects.nonNull(sessionProperties.getSchema())) {
                currSession = sessionFactory.withOptions().statementInspector(new SchemaInspector(sessionProperties.getSchema())).openSession();
                schema = sessionProperties.getSchema();
            } else {
                currSession = sessionFactory.openSession();
            }

            if (Objects.isNull(schema) && Objects.nonNull(sessionProperties.getDefaultSchema())) {
                schema = sessionProperties.getDefaultSchema();
            }

            return new DBSession(currSession, sessionProperties.getHibernateResource(), schema);
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail(getLangValue("hibernate.connect.to.db.error").formatted(e.getMessage()));
        }

        return null;
    }
}
