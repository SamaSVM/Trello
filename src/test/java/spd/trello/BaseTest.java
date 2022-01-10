package spd.trello;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public abstract class BaseTest {
    protected static HikariDataSource dataSource;

    @BeforeAll
    public static void init() {
//        HikariConfig cfg =new HikariConfig();
//        cfg.setPassword("postgres");
//        cfg.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
//        cfg.setUsername("postgres");
//        cfg.setDriverClassName("org.h2.Driver");
//        dataSource = new HikariDataSource(cfg);
//        Flyway flyway = Flyway.configure().locations("classpath:migration").dataSource(dataSource).load();
//        flyway.migrate();
        try {
            dataSource = ConnectionPool.createDataSource();
            FlywayMigrate.doMigrate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
