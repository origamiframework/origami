package ru.origami.hibernate.queries;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TemporalType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.query.BindableType;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import ru.origami.hibernate.models.EHibernateResource;
import ru.origami.hibernate.models.QueryParameter;
import ru.origami.hibernate.utils.Retry;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.hibernate.attachment.HibernateAttachment.*;
import static ru.origami.common.environment.Environment.*;

@Slf4j
public class DBCommonQuery<R> {

    protected Session session;

    protected Query query;

    protected String queryString;

    protected Retry retry;

    protected EHibernateResource resource;

    protected Date date = new Date();

    protected int attempt;

    protected List<String> profiles = new ArrayList<>();

    protected List<String> filters = new ArrayList<>();

    protected boolean withEmptyResult = false;

    protected boolean withNotEmptyResultList = false;

    protected List<QueryParameter> parameters = new ArrayList<>();

    protected List<QueryParameter> parameterCollectionList = new ArrayList<>();

    protected Integer maxResult;

    protected Integer firstResult;

    protected Class<?> resultType;

    protected DBCommonQuery(Session session, String query, EHibernateResource resource) {
        this.queryString = query;
        this.session = session;
        this.resource = resource;
        initRetry(resource);
    }

    protected DBCommonQuery(Session session, String query, Class<?> resultType, EHibernateResource resource) {
        this(session, query, resource);
        this.resultType = resultType;
    }

    public DBCommonQuery<R> setParameter(String name, Object value) {
        if (value == null) {
            queryString = replaceNullValue(queryString, name);
        } else {
            parameters.add(QueryParameter.Builder()
                    .setName(name)
                    .setValue(value)
                    .build());
        }

        return this;
    }

    public <P> DBCommonQuery<R> setParameter(String name, P value, BindableType<P> type) {
        if (value == null) {
            queryString = replaceNullValue(queryString, name);
        } else {
            parameters.add(QueryParameter.Builder()
                    .setName(name)
                    .setValue(value)
                    .setBindableType(type)
                    .build());
        }

        return this;
    }

    public <P> DBCommonQuery<R> setParameter(String name, P value, Class<P> type) {
        if (value == null) {
            queryString = replaceNullValue(queryString, name);
        } else {
            parameters.add(QueryParameter.Builder()
                    .setName(name)
                    .setValue(value)
                    .setClassType(type)
                    .build());
        }

        return this;
    }

    public DBCommonQuery<R> setParameter(String name, Instant argument, TemporalType temporalType) {
        if (argument == null) {
            queryString = replaceNullValue(queryString, name);
        } else {
            parameters.add(QueryParameter.Builder()
                    .setName(name)
                    .setInstantArgument(argument)
                    .setTemporalType(temporalType)
                    .build());
        }

        return this;
    }

    public DBCommonQuery<R> setParameter(String name, Calendar argument, TemporalType temporalType) {
        if (argument == null) {
            queryString = replaceNullValue(queryString, name);
        } else {
            parameters.add(QueryParameter.Builder()
                    .setName(name)
                    .setCalendarArgument(argument)
                    .setTemporalType(temporalType)
                    .build());
        }

        return this;
    }

    public DBCommonQuery<R> setParameter(String name, Date argument, TemporalType temporalType) {
        if (argument == null) {
            queryString = replaceNullValue(queryString, name);
        } else {
            parameters.add(QueryParameter.Builder()
                    .setName(name)
                    .setDateArgument(argument)
                    .setTemporalType(temporalType)
                    .build());
        }

        return this;
    }

    public DBCommonQuery<R> setFilterParameter(String filterName, String paramName, Object value) {
        getEnabledFilter(filterName).setParameter(paramName, value);

        return this;
    }

    private DBCommonQuery<R> changeParameter(String name, Object value, boolean withEmptyValue) {
        if (!queryString.contains(String.format(":%s", name))) {
            fail(getLangValue("hibernate.change.param.not.found").formatted(name));
        }

        try {
            if (value == null) {
                if (withEmptyValue) {
                    queryString = queryString.replaceAll(String.format("(AND|OR) [\\w\\d\\.']+ (=)?(\s)?(')?:%s(')?", name), "");
                } else {
                    queryString = replaceNullValue(queryString, name);
                }
            } else {
                queryString = queryString.replaceAll(String.format(":(\s)?%s", name), String.valueOf(value));
            }
        } catch (Exception ex) {
            changeParameterFail(name, ex);
        }

        return this;
    }

    /**
     * This is a modification of changeParameter(String name, Object value),
     * used when you need to remove the entire line if the passed value is null.
     * <p>
     * Example: AND d.insurance = :insuranceParam
     * If insuranceParam is null, this line will be next -> "".
     */
    public DBCommonQuery<R> changeParameterWithEmptyValue(String name, Object value) {
        changeParameter(name, value, true);

        return this;
    }

    /**
     * Replaces two colons and occurrences (name) with the supplied value.
     * Where you previously used String.format, you can now use changeParameter().
     * <p>
     * If in detail then null will be replaced with NULL in the INSERT, but there will be no replacement for IS NULL.
     * In SELECT and DELETE, constructs like "=: key" are replaced by IS NULL, and ": key" by NULL.
     * In UPDATE, the values ": key" before WHERE are replaced with NULL,
     * after WHERE there will be a replacement of "=: key" with IS NULL.
     * See more in replaceNullValue().
     */
    public DBCommonQuery<R> changeParameter(String name, Object value) {
        changeParameter(name, value, false);

        return this;
    }

    public DBCommonQuery<R> setParameterList(String name, Collection<?> values) {
        parameterCollectionList.add(QueryParameter.Builder()
                .setName(name)
                .setCollectionValues(values)
                .build());

        return this;
    }

    public <P> DBCommonQuery<R> setParameterList(String name, Collection<? extends P> arguments, Class<P> javaType) {
        parameterCollectionList.add(QueryParameter.Builder()
                .setName(name)
                .setCollectionValues(arguments)
                .setClassType(javaType)
                .build());

        return this;
    }

    public <P> DBCommonQuery<R> setParameterList(String name, Collection<? extends P> arguments, BindableType<P> type) {
        parameterCollectionList.add(QueryParameter.Builder()
                .setName(name)
                .setCollectionValues(arguments)
                .setBindableType(type)
                .build());

        return this;
    }

    public DBCommonQuery<R> setParameterList(String name, Object[] values) {
        parameterCollectionList.add(QueryParameter.Builder()
                .setName(name)
                .setMassiveValues(values)
                .build());

        return this;
    }

    public <P> DBCommonQuery<R> setParameterList(String name, P[] arguments, Class<P> javaType) {
        parameterCollectionList.add(QueryParameter.Builder()
                .setName(name)
                .setMassiveValues(arguments)
                .setClassType(javaType)
                .build());

        return this;
    }

    public <P> DBCommonQuery<R> setParameterList(String name, P[] arguments, BindableType<P> type) {
        parameterCollectionList.add(QueryParameter.Builder()
                .setName(name)
                .setMassiveValues(arguments)
                .setBindableType(type)
                .build());

        return this;
    }

    public DBCommonQuery<R> setMaxResults(int maxResult) {
        if (maxResult <= 0) {
            endTransaction();
            fail(getLangValue("hibernate.max.result.zero.value.error"));
        }

        this.maxResult = maxResult;

        return this;
    }

    public DBCommonQuery<R> setFirstResult(int startPosition) {
        this.firstResult = startPosition;

        return this;
    }

    public R getSingleResult() {
        createQuery();
        Object result = null;
        Exception exception = null;

        try {
            if (retry != null) {
                waitBeforeExecute();

                do {
                    attempt++;
                    logAttempt(attempt);

                    try {
                        result = query.getSingleResult();
                    } catch (NoResultException ex) {
                    }
                } while (result == null && possibleToFulfillRequest());
            } else {
                result = query.getSingleResult();
            }
        } catch (Exception ex) {
            exception = ex;
        }

        checkResult(result, exception);

        return (R) result;
    }

    public R uniqueResult() {
        createQuery();
        Object result = null;
        Exception exception = null;

        try {
            if (retry != null) {
                waitBeforeExecute();

                do {
                    attempt++;
                    logAttempt(attempt);

                    try {
                        result = query.uniqueResult();
                    } catch (NoResultException ex) {
                    }
                } while (result == null && possibleToFulfillRequest());
            } else {
                result = query.uniqueResult();
            }
        } catch (Exception ex) {
            exception = ex;
        }

        checkResult(result, exception);

        return (R) result;
    }

    public Optional<R> uniqueResultOptional() {
        createQuery();
        Optional<Object> result = null;
        Exception exception = null;

        try {
            if (retry != null) {
                waitBeforeExecute();

                do {
                    attempt++;
                    logAttempt(attempt);

                    try {
                        result = query.uniqueResultOptional();
                    } catch (NoResultException ex) {
                    }
                } while (result == null && possibleToFulfillRequest());
            } else {
                result = query.uniqueResultOptional();
            }
        } catch (Exception ex) {
            exception = ex;
        }

        checkResult(result == null ? null : result.orElse(null), exception);

        return (Optional<R>) result;
    }

    public Stream<R> getResultStream() {
        createQuery();
        Stream<Object> result = null;
        Exception exception = null;

        try {
            if (retry != null) {
                waitBeforeExecute();

                do {
                    attempt++;
                    logAttempt(attempt);

                    result = query.getResultStream();
                } while (result == null && possibleToFulfillRequest());
            } else {
                result = query.getResultStream();
            }
        } catch (Exception ex) {
            exception = ex;
        }

        List<Object> resultList = result == null ? null : result.collect(Collectors.toList());
        checkResultForList(resultList, result == null ? 0 : result.count(), exception);

        return (Stream<R>) result;
    }

    public List<R> getResultList() {
        createQuery();
        List<Object> result = null;
        Exception exception = null;

        try {
            if (retry != null) {
                waitBeforeExecute();

                do {
                    attempt++;
                    logAttempt(attempt);

                    result = query.getResultList();
                } while (result == null && possibleToFulfillRequest());
            } else {
                result = query.getResultList();
            }
        } catch (Exception ex) {
            exception = ex;
        }

        checkResultForList(result, result == null ? 0 : result.size(), exception);

        return (List<R>) result;
    }

    public List<R> getNotEmptyResultList() {
        withNotEmptyResultList = true;

        return getResultList();
    }

    public void executeUpdate() {
        createQuery();

        try {
            int result = query.executeUpdate();
            endTransaction();
            String lowerQuery = query.getQueryString().toLowerCase();

            if (lowerQuery.startsWith("update") || lowerQuery.startsWith("delete") || lowerQuery.startsWith("insert")) {
                attachSqlResultToAllure(lowerQuery, result);
            }
        } catch (Exception ex) {
            executeUpdateFail(ex);
        }
    }

    protected void disableFetchProfiles() {
        while (profiles.size() > 0) {
            disableFetchProfile(profiles.get(0));
        }
    }

    protected void disableFilters() {
        while (filters.size() > 0) {
            disableFilter(filters.get(0));
        }
    }

    protected void endTransaction() {
        endTransaction(null);
    }

    protected void endTransaction(Exception ex) {
        endTransaction(ex, null);
    }

    protected void endTransaction(Exception ex, String message) {
        if (!session.getTransaction().getStatus().isOneOf(TransactionStatus.COMMITTED)) {
            try {
                session.getTransaction().commit();
                session.clear();
            } catch (Exception exception) {
            }
        }

        try {
            attachSqlQueryToAllure(queryString, parameters);
        } catch (Exception e) {
        }

        try {
            disableFetchProfiles();
            disableFilters();
            disableEmptyResult();
            withNotEmptyResultList = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exceptionFail(ex, message);
    }

    public DBCommonQuery<R> enableFilter(String filterName) {
        session.enableFilter(filterName);
        filters.add(filterName);

        return this;
    }

    public Filter getEnabledFilter(String filterName) {
        return session.getEnabledFilter(filterName);
    }

    public DBCommonQuery<R> disableFilter(String filterName) {
        session.disableFilter(filterName);
        filters.remove(filterName);

        return this;
    }

    public DBCommonQuery<R> enableFetchProfile(String profileName) {
        session.enableFetchProfile(profileName);
        profiles.add(profileName);

        return this;
    }

    public DBCommonQuery<R> disableFetchProfile(String profileName) {
        session.disableFetchProfile(profileName);
        profiles.remove(profileName);

        return this;
    }

    protected void initRetry() {
        this.retry = new Retry();
    }

    protected void initRetry(EHibernateResource resource) {
        this.retry = resource == EHibernateResource.CLICKHOUSE ? new Retry() : null;
    }

    public DBCommonQuery<R> setRetryStopMaxAttempt(long stopMaxAttemptInSeconds) {
        if (retry == null) {
            initRetry();
        }

        retry.setStopMaxAttempt(stopMaxAttemptInSeconds);

        return this;
    }

    public DBCommonQuery<R> setRetryStopMaxDelay(long stopMaxDelayInSeconds) {
        if (retry == null) {
            initRetry();
        }

        retry.setStopMaxDelay(stopMaxDelayInSeconds);

        return this;
    }

    public DBCommonQuery<R> setRetryWait(long waitInSeconds) {
        if (retry == null) {
            initRetry();
        }

        retry.setWait(waitInSeconds);

        return this;
    }

    protected void waitRetry(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void waitBeforeExecute() {
        waitRetry(retry.getWaitBeforeExecute());
    }

    protected boolean possibleToFulfillRequest() {
        waitRetry(retry.getWait());

        return attempt < retry.getStopMaxAttempt()
                && new Date().before(new Date(date.getTime() + (retry.getStopMaxDelay() * 1000)));
    }

    public DBCommonQuery<R> enableEmptyResult() {
        this.withEmptyResult = true;

        return this;
    }

    public DBCommonQuery<R> disableEmptyResult() {
        this.withEmptyResult = false;

        return this;
    }

    protected void checkResult(Object result, Exception ex) {
        boolean withEmptyResult = this.withEmptyResult;
        endTransaction(ex);

        if (result == null && !withEmptyResult) {
            fail(getLangValue("hibernate.empty.result.query"));
        }

        attachSqlResultToAllure(result == null ? null : List.of(result));
    }

    protected void checkResultForList(List<Object> result, long count, Exception ex) {
        boolean withNotEmptyResultList = this.withNotEmptyResultList;
        endTransaction(ex);

        if (count == 0 && withNotEmptyResultList) {
            fail(getLangValue("hibernate.empty.result.query"));
        }

        attachSqlResultToAllure(result);
    }

    private void exceptionFail(Exception ex, String message) {
        String failMessage = message == null ? "" : message;

        if (ex != null && !(ex instanceof NoResultException)) {
            attachExceptionToAllure(ex.getClass().getName(), ExceptionUtils.getStackTrace(ex));
            ex.printStackTrace();

            if (ex instanceof ClassCastException) {
                ex.printStackTrace();
                fail(getLangValue("hibernate.not.valid.param.set.error").formatted(ex.getMessage()));
            } else {
                fail("%s%s".formatted(failMessage, ex.getMessage()));
            }
        }
    }

    protected void executeUpdateFail(Exception ex) {
        endTransaction(ex, getLangValue("hibernate.execute.query.error"));
    }

    protected void setParameterFail(String key, Exception ex) {
        endTransaction(ex, getLangValue("hibernate.set.param.error").formatted(key));
    }

    protected void setMaxResultFail(Exception ex) {
        endTransaction(ex, getLangValue("hibernate.max.result.value.error"));
    }

    protected void setFirstResultFail(Exception ex) {
        endTransaction(ex, getLangValue("hibernate.first.result.value.error"));
    }

    protected void changeParameterFail(String key, Exception ex) {
        endTransaction(ex, getLangValue("hibernate.change.param.error").formatted(key));
    }

    protected void querySyntaxFail(Exception ex) {
        endTransaction(ex, getLangValue("hibernate.query.error"));
    }

    protected String replaceNullValue(String query, String name) {
        if (query.toUpperCase().startsWith("INSERT")) {
            return query.replaceAll(String.format("(')?:%s(')?", name), "NULL");
        } else if (query.toUpperCase().startsWith("SELECT") || query.toUpperCase().startsWith("DELETE")) {
            return query.replaceAll(String.format("=(\\s)?(')?(\\s)?:%s(')?", name), "IS NULL")
                    .replaceAll(String.format("(')?:%s(')?", name), "NULL");
        } else if (query.toUpperCase().startsWith("UPDATE")) {
            String beforeWhere = query.substring(0, query.toUpperCase().indexOf("WHERE"))
                    .replaceAll(String.format(":%s", name), "NULL");
            String afterWhere = query.substring(query.toUpperCase().indexOf("WHERE"))
                    .replaceAll(String.format("=(\\s)?:%s", name), "IS NULL");

            return beforeWhere + afterWhere;
        }

        return query.replaceAll(String.format("(')?:%s(')?", name), "NULL");
    }

    protected void createQuery() {
        try {
            if (this instanceof DBNativeQuery) {
                if (resultType != null) {
                    query = session.createNativeQuery(queryString, resultType);
                } else {
                    query = session.createNativeQuery(queryString);
                }
            } else {
                if (resultType != null) {
                    query = session.createQuery(queryString, resultType);
                } else {
                    query = session.createQuery(queryString);
                }
            }
        } catch (Exception ex) {
            endTransaction(ex);
        }

        if (maxResult != null) {
            try {
                query.setMaxResults(maxResult);
            } catch (Exception e) {
                setMaxResultFail(e);
            }
        }

        if (firstResult != null) {
            try {
                query.setFirstResult(firstResult);
            } catch (Exception e) {
                setFirstResultFail(e);
            }
        }

        for (QueryParameter param : parameters) {
            try {
                if (Objects.nonNull(param.getInstantArgument())) {
                    query.setParameter(param.getName(), param.getInstantArgument(), param.getTemporalType());
                } else if (Objects.nonNull(param.getCalendarArgument())) {
                    query.setParameter(param.getName(), param.getCalendarArgument(), param.getTemporalType());
                } else if (Objects.nonNull(param.getDateArgument())) {
                    query.setParameter(param.getName(), param.getDateArgument(), param.getTemporalType());
                } else if (Objects.nonNull(param.getBindableType())) {
                    query.setParameter(param.getName(), param.getValue(), param.getBindableType());
                }  else if (Objects.nonNull(param.getClassType())) {
                    query.setParameter(param.getName(), param.getValue(), param.getClassType());
                } else {
                    query.setParameter(param.getName(), param.getValue());
                }
            } catch (Exception e) {
                setParameterFail(param.getName(), e);
            }
        }

        for (QueryParameter param : parameterCollectionList) {
            try {
                if (Objects.nonNull(param.getCollectionValues())) {
                    if (Objects.nonNull(param.getClassType())) {
                        query.setParameterList(param.getName(), param.getCollectionValues(), param.getClassType());
                    } else if (Objects.nonNull(param.getBindableType())) {
                        query.setParameterList(param.getName(), param.getCollectionValues(), param.getBindableType());
                    } else {
                        query.setParameterList(param.getName(), param.getCollectionValues());
                    }
                } else if (Objects.nonNull(param.getMassiveValues())) {
                    if (Objects.nonNull(param.getClassType())) {
                        query.setParameterList(param.getName(), param.getMassiveValues(), param.getClassType());
                    } else if (Objects.nonNull(param.getBindableType())) {
                        query.setParameterList(param.getName(), param.getMassiveValues(), param.getBindableType());
                    } else {
                        query.setParameterList(param.getName(), param.getMassiveValues());
                    }
                }
            } catch (Exception e) {
                setParameterFail(param.getName(), e);
            }
        }
    }

    private void logAttempt(int attempt) {
        if (isLocal() || isLoggingEnabled()) {
            log.info(getLangValue("hibernate.attempt"), attempt);
        }
    }
}
