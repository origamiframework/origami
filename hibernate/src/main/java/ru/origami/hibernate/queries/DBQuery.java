package ru.origami.hibernate.queries;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import ru.origami.hibernate.models.EHibernateResource;

@Slf4j
public class DBQuery<R> extends DBCommonQuery<DBQuery<R>> {

    public DBQuery(Session session, String query, EHibernateResource resource) {
        super(session, query, resource);
    }

    public DBQuery(Session session, String query, Class<?> resultType, EHibernateResource resource) {
        super(session, query, resultType, resource);
    }
}
