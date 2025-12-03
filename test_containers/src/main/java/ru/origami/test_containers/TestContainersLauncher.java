package ru.origami.test_containers;

import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.opentest4j.AssertionFailedError;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import ru.origami.common.environment.Environment;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;

public class TestContainersLauncher implements LauncherSessionListener {

    private static final String CONTAINERS_PKG_PREFIX = "test.containers.pkg.prefix";
    private static final String CI_CONTAINERS_PKG_PREFIX = "TEST_CONTAINERS_PKG_PREFIX";

    private static final String CONTAINERS_NAME = "test.containers.name";
    private static final String CI_CONTAINERS_NAME = "TEST_CONTAINERS_NAME";

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    private static AssertionFailedError error = null;

    @Override
    public void launcherSessionOpened(LauncherSession session) throws AssertionFailedError {
        synchronized (TestContainersLauncher.class) {
            if (Objects.nonNull(error)) {
                throw error;
            }

            if (STARTED.getAndSet(true)) {
                return;
            }

            if ("true".equalsIgnoreCase(Environment.TEST_CONTAINERS_ENABLED)) {
                try {
                    TestContainers impl = selectImplementation();
                    impl.startIfNeeded();
                } catch (AssertionFailedError ex) {
                    error = ex;
                    throw ex;
                }
            }
        }
    }

    private TestContainers selectImplementation() {
        // Необязательное сужение по пакету
        String pkgPrefix = getSysEnvPropertyOrDefault(CONTAINERS_PKG_PREFIX, CI_CONTAINERS_PKG_PREFIX, "").trim();

        // Ищем всех наследников TestContainers
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.SubTypes)
        );

        Set<Class<? extends TestContainers>> subs = reflections.getSubTypesOf(TestContainers.class)
                .stream()
                .filter(c -> !c.isInterface() && !java.lang.reflect.Modifier.isAbstract(c.getModifiers()))
                .filter(c -> pkgPrefix.isEmpty() || c.getName().startsWith(pkgPrefix))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (subs.isEmpty()) {
            throw new ExtensionConfigurationException(getLangValue("test.containers.no.implementation.error")
                    .formatted(pkgPrefix.isEmpty()
                            ? ""
                            : getLangValue("test.containers.no.implementation.filter").formatted(pkgPrefix)));
        }

        String wanted = getSysEnvPropertyOrDefault(CONTAINERS_NAME, CI_CONTAINERS_NAME, "").trim();

        List<Class<? extends TestContainers>> candidates;

        if (wanted.isEmpty()) {
            // Если ровно один — берём; если больше — требуем containers.name
            if (subs.size() == 1) {
                candidates = List.copyOf(subs);
            } else {
                throw multipleFoundError(subs, null, getLangValue("test.containers.multiple.found.error")
                        .formatted(pkgPrefix.isEmpty() ? getLangValue("test.containers.multiple.found.error.pkg") : ""));
            }
        } else {
            candidates = matchByName(subs, wanted);

            if (candidates.isEmpty()) {
                throw new ExtensionConfigurationException(getLangValue("test.containers.no.implementation.by.name.error")
                        .formatted(wanted, list(subs)));
            }

            if (candidates.size() > 1) {
                throw multipleFoundError(new LinkedHashSet<>(candidates), wanted, getLangValue("test.containers.multiple.found.by.name.error")
                        .formatted(wanted, pkgPrefix.isEmpty() ? getLangValue("test.containers.multiple.found.by.name.error.prefix") : ""));
            }
        }

        return newInstance(candidates.get(0));
    }

    private static List<Class<? extends TestContainers>> matchByName(Set<Class<? extends TestContainers>> subs, String wanted) {
        // 1) Точное совпадение по FQN
        List<Class<? extends TestContainers>> fqn = subs.stream()
                .filter(c -> c.getName().equals(wanted))
                .collect(Collectors.toList());

        if (!fqn.isEmpty()) {
            return fqn;
        }

        // 2) Совпадение по @TestContainerName (если аннотация есть)
        List<Class<? extends TestContainers>> byAnno = subs.stream()
                .filter(c -> {
                    TestContainerName ann = c.getAnnotation(TestContainerName.class);

                    return ann != null && ann.value().equalsIgnoreCase(wanted);
                })
                .collect(Collectors.toList());

        if (!byAnno.isEmpty()) {
            return byAnno;
        }

        // 3) Совпадение по simpleName (без пакета)
        List<Class<? extends TestContainers>> simple = subs.stream()
                .filter(c -> c.getSimpleName().equals(wanted))
                .collect(Collectors.toList());

        return simple;
    }

    private static ExtensionConfigurationException multipleFoundError(Set<Class<? extends TestContainers>> subs,
                                                                      String wanted, String hint) {
        String header = wanted == null ? "" : getLangValue("test.containers.looking.name").formatted(wanted);

        return new ExtensionConfigurationException(
                getLangValue("test.containers.looking.candidates").formatted(header, list(subs), hint));
    }

    private static String list(Set<Class<? extends TestContainers>> subs) {
        return subs.stream().map(c -> {
            TestContainerName ann = c.getAnnotation(TestContainerName.class);
            String tag = ann == null ? "" : "  (@%s)".formatted(ann.value());

            return " - %s [%s]%s".formatted(c.getName(), c.getSimpleName(), tag);
        }).collect(Collectors.joining("\n"));
    }

    private static TestContainers newInstance(Class<? extends TestContainers> cls) {
        try {
            Constructor<? extends TestContainers> ctor = cls.getDeclaredConstructor();
            ctor.setAccessible(true);

            return ctor.newInstance();
        } catch (Exception e) {
            throw new ExtensionConfigurationException(
                    getLangValue("test.containers.new.instance.error").formatted(cls.getName(), e.getMessage()), e);
        }
    }
}
