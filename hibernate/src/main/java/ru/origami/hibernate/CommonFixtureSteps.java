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

    private static Map<Thread, Map<Class, DBSession>> dbSessions = new HashMap<>();

    protected void initSession() {
        Thread currentThread = Thread.currentThread();

        synchronized (dbSessions) {
            if (!dbSessions.containsKey(currentThread)) {
                dbSessions.put(currentThread, new HashMap<>());
            }

            if (!dbSessions.get(currentThread).containsKey(this.getClass())) {
                dbSessions.get(currentThread).put(this.getClass(), openDataBaseConnection());
            }
        }

        session = dbSessions.get(currentThread).get(this.getClass());
    }

    private DBSession openDataBaseConnection() {
        if (sessionProperties == null) {
            fail(getLangValue("hibernate.props.null"));
        }

        try {
            SessionFactory sessionFactory = DataBaseConnection.getSessionFactory(sessionProperties);
            Session currSession;

            if (Objects.nonNull(sessionProperties.getSchema())) {
                currSession = sessionFactory.withOptions().statementInspector(new SchemaInspector(sessionProperties.getSchema())).openSession();
            } else {
                currSession = sessionFactory.openSession();
            }

            return new DBSession(currSession, sessionProperties.getHibernateResource());
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail(getLangValue("hibernate.connect.to.db.error").formatted(e.getMessage()));
        }

        return null;
    }
}
