package ru.origami.hibernate.attachment;

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HibernateLoggerFile {

    static final String LOG_FILES_PATH = "target/logs";

    private static final String ROOT_LOG_FILE_PATH = String.format("%s/%s", System.getProperty("user.dir"), LOG_FILES_PATH);

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();

    static {
        File file = new File(ROOT_LOG_FILE_PATH);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Deprecated
    public static void tryLockSqlLogging() {
        while (true) {
            if (!((ReentrantReadWriteLock) RW_LOCK).isWriteLocked()) {
                RW_LOCK.writeLock().lock();
                break;
            }
        }
    }

    @Deprecated
    public static void unlockSqlLogging() {
        try {
            RW_LOCK.writeLock().unlock();
        } catch (IllegalMonitorStateException ex) {
            ex.printStackTrace();
        }
    }
}
