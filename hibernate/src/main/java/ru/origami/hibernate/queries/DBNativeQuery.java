package ru.origami.hibernate.queries;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import ru.origami.hibernate.models.EHibernateResource;

@Slf4j
public class DBNativeQuery<R> extends DBCommonQuery<DBNativeQuery<R>> {

    public DBNativeQuery(Session session, String query, EHibernateResource resource, String schema) {
        super(session, query, resource, schema);
    }

    public DBNativeQuery(Session session, String query, Class<?> resultType, EHibernateResource resource, String schema) {
        super(session, query, resultType, resource, schema);
    }
}
