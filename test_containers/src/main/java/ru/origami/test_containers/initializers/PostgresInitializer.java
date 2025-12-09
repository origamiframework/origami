package ru.origami.test_containers.initializers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

@Slf4j
public final class PostgresInitializer {

    private PostgresInitializer() {
    }

    public static void migrate(PostgreSQLContainer<?> container, List<String> locations) {
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
