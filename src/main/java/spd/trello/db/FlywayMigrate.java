package spd.trello.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayMigrate {

    public static void doMigrate() {
        Flyway flyway = createFlyway(ConnectionPool.createDataSource());
        flyway.migrate();
    }

    private static Flyway createFlyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .load();
    }
}
