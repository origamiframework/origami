package ru.origami.hibernate.log4jdbc.log;

import ru.origami.hibernate.log4jdbc.Properties;
import ru.origami.hibernate.log4jdbc.sql.Spy;

/**
 * Class implementing logics common to all {@code SpyLogDelegator} implementations, 
 * whatever the logging library used is.
 */
public abstract class AbstractSpyLogDelegator implements ru.origami.hibernate.log4jdbc.log.SpyLogDelegator {
    /**
     * Default constructor.
     */
    public AbstractSpyLogDelegator() {
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation performs the filtering of exceptions following a methodCall equal to 
     * {@code SpyLogDelegator#GET_GENERATED_KEYS_METHOD_CALL}, if the property 
     * {@code log4jdbc.suppress.generated.keys.exception} is {@code true}. If the exception 
     * is allowed to be logged, this method will then delegate to the method 
     * {@link #filteredExceptionOccured(Spy, String, Exception, String, long)}.
     * 
     * @see ru.origami.hibernate.log4jdbc.log.SpyLogDelegator#exceptionOccured(Spy, String, Exception, String, long)
     * @see #filteredExceptionOccured(Spy, String, Exception, String, long)
     */
    @Override
    public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
        if (Properties.isSuppressGetGeneratedKeysException() && methodCall.equals(GET_GENERATED_KEYS_METHOD_CALL)) {
            return;
        }

        this.filteredExceptionOccured(spy, methodCall, e, sql, execTime);
    }
    
    /**
     * This method is called following a call to 
     * {@link #exceptionOccured(Spy, String, Exception, String, long)}, 
     * if the exception is allowed to be logged. So this is the method performing 
     * the actual logging. See {@link #exceptionOccured(Spy, String, Exception, String, long)} 
     * for details about the filtering. 
     * 
     * @param spy           see {@link ru.origami.hibernate.log4jdbc.log.SpyLogDelegator#exceptionOccured(Spy, String, Exception, String, long)}.
     * @param methodCall    see {@link ru.origami.hibernate.log4jdbc.log.SpyLogDelegator#exceptionOccured(Spy, String, Exception, String, long)}.
     * @param e             see {@link ru.origami.hibernate.log4jdbc.log.SpyLogDelegator#exceptionOccured(Spy, String, Exception, String, long)}.
     * @param sql           see {@link ru.origami.hibernate.log4jdbc.log.SpyLogDelegator#exceptionOccured(Spy, String, Exception, String, long)}.
     * @param execTime      see {@link SpyLogDelegator#exceptionOccured(Spy, String, Exception, String, long)}
     * @see #exceptionOccured(Spy, String, Exception, String, long)
     */
    protected abstract void filteredExceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime);
}
