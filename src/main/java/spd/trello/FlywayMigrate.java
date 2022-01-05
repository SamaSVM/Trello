package spd.trello;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;

public class FlywayMigrate {
    public static void doMigrate() throws IOException {
        Flyway flyway = createFlyway(ConnectionPool.createDataSource());
        flyway.migrate();
    }

    private static Flyway createFlyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .load();
    }
}
