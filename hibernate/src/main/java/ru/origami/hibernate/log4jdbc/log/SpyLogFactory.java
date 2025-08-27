package ru.origami.hibernate.log4jdbc.log;

import ru.origami.hibernate.log4jdbc.Properties;
import ru.origami.hibernate.log4jdbc.log.log4j2.Log4j2SpyLogDelegator;

/**
 * A provider for a SpyLogDelegator.  This allows a single switch point to abstract
 * away which logging system to use for spying on JDBC calls.
 * <p>
 * Modifications for log4j2: 
 * <ul>
 * <li>addition of the <code>#loadSpyLogDelegator()</code> method 
 * to choose between the new <code>Log4j2SpyLogDelegator</code>, 
 * or an alternative <code>SpyLogDelegator</code>. 
 * This method uses <code>ru.origami.hibernate.log4jdbc.log4j2.Properties#getSpyLogDelegatorName()</code> 
 * to determine which logger to use. 
 * <li>Use of this method to set the <code>logger</code> attribute 
 * if <code>null</code> when calling <code>#getSpyLogDelegator()</code>.
 * <li>From load4jdbc-remix, addition of a method to set a custom <code>SpyLogDelegator</code>
 * </ul>
 */
public class SpyLogFactory {

    /**
     * Do not allow instantiation.  Access is through static method.
     */
    private SpyLogFactory() {}

    /**
     * The logging system of choice.
     * Default value is Log4j2SpyLogDelegator
     */
    private static ru.origami.hibernate.log4jdbc.log.SpyLogDelegator logger;

    /**
     * Return the <code>SpyLogDelegator</code>. 
     * If not already initialized (for instance, using 
     * {@link #setSpyLogDelegator(ru.origami.hibernate.log4jdbc.log.SpyLogDelegator)}), this getter will load it first,
     * using the {@link ru.origami.hibernate.log4jdbc.log4j2.Properties#getSpyLogDelegatorName()}. 
     * If the name is <code>null</code>, load {@link ru.origami.hibernate.log4jdbc.log4j2.Log4j2SpyLogDelegator}, 
     * otherwise, try to load the corresponding class. 
     * 
     * @return 	The <code>SpyLogDelegator</code> to use.
     * @see #setSpyLogDelegator(ru.origami.hibernate.log4jdbc.log.SpyLogDelegator)
     */  
    public static ru.origami.hibernate.log4jdbc.log.SpyLogDelegator getSpyLogDelegator() {
        if (logger == null) {
            loadSpyLogDelegator(Properties.getSpyLogDelegatorName());
        }

        return logger;
    }  

    /**
     * Set the appropriate <code>SpyLogDelegator</code> 
     * depending on <code>spyLogDelegatorName</code>. 
     * If <code>null</code>, load {@link ru.origami.hibernate.log4jdbc.log4j2.Log4j2SpyLogDelegator}, 
     * otherwise, try to load the corresponding class.  
     * 
     * @param spyLogDelegatorName 	A <code>String</code> representing the name 
     * 								of the class implementing <code>SpyLogDelegator</code> 
     * 								to load. If <code>null</code>, 
     * 								load <code>Log4j2SpyLogDelegator</code>.
     * @see Slf4jSpyLogDelegator
     * @see ru.origami.hibernate.log4jdbc.log4j2.Log4j2SpyLogDelegator
     */ 
    public static void loadSpyLogDelegator(String spyLogDelegatorName) {
        if (spyLogDelegatorName == null) {
            try {
                setSpyLogDelegator(new Log4j2SpyLogDelegator());
            } catch(NoClassDefFoundError e){
                throw new NoClassDefFoundError("Unable to find Log4j2 as default logging library. " +
                		"Please provide a logging library and configure a valid spyLogDelegator name in the properties file.");
            }
        } else {
            try {
                Object loadedClass = 
                        Class.forName(spyLogDelegatorName).newInstance();

                if (loadedClass == null) {
                    throw new IllegalArgumentException("spyLogDelegatorName loads a null SpyLogDelegator");
                }

                setSpyLogDelegator((ru.origami.hibernate.log4jdbc.log.SpyLogDelegator) loadedClass);
            } catch (Exception e) {
                throw new IllegalArgumentException("spyLogDelegatorName does not allow to load a valid SpyLogDelegator: " + e.getMessage());
            } catch (NoClassDefFoundError e) {
            	throw new NoClassDefFoundError("Cannot find a library corresponding to the property log4jdbc.spylogdelegator.name. " +
                		"Please provide a logging library and configure a valid spyLogDelegator name in the properties file.");
            }
        }
    } 

    /**
     * @param logDelegator the log delegator responsible for actually logging
     * JDBC events.
     */
    public static void setSpyLogDelegator(SpyLogDelegator logDelegator) {
        if (logDelegator == null) {
            throw new IllegalArgumentException("log4jdbc: logDelegator cannot be null.");
        }

        logger = logDelegator;
    }  
}

