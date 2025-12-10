package ru.origami.test_containers.initializers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.List;

@Slf4j
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void migrate(JdbcDatabaseContainer<?> container, List<String> locations) {
        if (CollectionUtils.isNotEmpty(locations)) {
            Flyway flyway = Flyway.configure()
                    .dataSource(container.getJdbcUrl(), container.getUsername(), container.getPassword())
                    .locations(locations.stream()
                            .map("classpath:%s"::formatted)
                            .toArray(String[]::new)) // пример db/migration
                    .sqlMigrationPrefix("v")
                    .repeatableSqlMigrationPrefix("r")
                    .load();

            flyway.migrate();
        }
    }
}
