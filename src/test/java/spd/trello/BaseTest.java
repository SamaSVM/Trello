package spd.trello;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public abstract class BaseTest {

    protected static HikariDataSource dataSource;

    @BeforeAll
    public static void init() {
        try {
            dataSource = ConnectionPool.createDataSource();
            FlywayMigrate.doMigrate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
