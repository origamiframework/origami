package ru.origami.test_containers.initializers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.JdbcDatabaseContainer;
import ru.origami.test_containers.TestContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.test_containers.TestContainersLauncher.getExecutionParallelThreads;

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

    public static void createPostgreSQLSchemas(TestContainer testContainer) {
        if (Objects.nonNull(testContainer.getPostgreSQLSchema())) {
            JdbcDatabaseContainer<?> container = testContainer.getDatabaseContainer();
            int threads = getExecutionParallelThreads();

            try (Connection conn = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
                 Statement stmt = conn.createStatement()) {
                for (int i = 1; i <= threads; i++) {
                    stmt.execute("CREATE SCHEMA IF NOT EXISTS %s;".formatted(getSchemaName(testContainer, i)));
                }
            } catch (Exception e) {
                throw new RuntimeException(getLangValue("test.containers.fail.create.db.schema"), e);
            }
        }
    }

    public static String getSchemaName(TestContainer testContainer, int threadNum) {
        return "%s_thread_%d;".formatted(testContainer.getPostgreSQLSchema(), threadNum);
    }
}
