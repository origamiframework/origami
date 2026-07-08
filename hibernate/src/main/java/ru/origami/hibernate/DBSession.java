package ru.origami.hibernate;

import org.apache.logging.log4j.ThreadContext;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import ru.origami.hibernate.models.EHibernateResource;
import ru.origami.hibernate.queries.DBCommonQuery;
import ru.origami.hibernate.queries.DBNativeQuery;
import ru.origami.hibernate.queries.DBQuery;

import static ru.origami.hibernate.attachment.HibernateAttachment.attachSqlQueryToAllure;

public class DBSession {

    Session session;

    EHibernateResource resource;

    String schema;

    public DBSession(Session session, EHibernateResource resource, String schema) {
        ThreadContext.put("logFileName", Thread.currentThread().getName());

        this.session = session;
        this.resource = resource;
        this.schema = schema;
    }

    private synchronized void beginTransaction() {
        if (!session.getTransaction().getStatus().isOneOf(TransactionStatus.ACTIVE)) {
            try {
                session.beginTransaction();
            } catch (Exception ex) {
            }
        }
    }

    protected void endTransaction() {
        if (!session.getTransaction().getStatus().isOneOf(TransactionStatus.COMMITTED)) {
            try {
                session.getTransaction().commit();
                session.clear();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        try {
            attachSqlQueryToAllure(null, null, schema);
        } catch (Exception ex) {
        }
    }

    protected Transaction getTransaction() {
        return session.getTransaction();
    }

    public <T> DBCommonQuery<T> createQuery(String queryString) {
        return new DBQuery(session, queryString, resource, schema);
    }

    public <T> DBCommonQuery<T> createQuery(String queryString, Class<T> resultType) {
        return new DBQuery(session, queryString, resultType, resource, schema);
    }

    public <R> DBCommonQuery<R> createNativeQuery(String sqlString) {
        return new DBNativeQuery(session, sqlString, resource, schema);
    }

    public <R> DBCommonQuery<R> createNativeQuery(String sqlString, Class<R> resultType) {
        return new DBNativeQuery(session, sqlString, resultType, resource, schema);
    }

    private Object save(Object object) {
        try {
            beginTransaction();

            return session.save(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }

        return null;
    }

    private Object save(String entityName, Object object) {
        try {
            beginTransaction();

            return session.save(entityName, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }

        return null;
    }

    private void saveOrUpdate(Object object) {
        try {
            beginTransaction();
            session.saveOrUpdate(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void saveOrUpdate(String entityName, Object object) {
        try {
            beginTransaction();
            session.saveOrUpdate(entityName, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void update(Object object) {
        try {
            beginTransaction();
            session.update(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void update(String entityName, Object object) {
        try {
            beginTransaction();
            session.update(entityName, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private Object merge(Object object) {
        try {
            beginTransaction();

            return session.merge(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }

        return null;
    }

    private Object merge(String entityName, Object object) {
        try {
            beginTransaction();
            
            return session.merge(entityName, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }

        return null;
    }

    private void delete(Object object) {
        try {
            beginTransaction();
            session.delete(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void delete(String entityName, Object object) {
        try {
            beginTransaction();
            session.delete(entityName, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void close() {
        session.close();
    }
}
