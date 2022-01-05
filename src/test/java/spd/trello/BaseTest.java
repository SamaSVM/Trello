package spd.trello;

import org.junit.jupiter.api.BeforeAll;

import javax.sql.DataSource;
import java.io.IOException;

public abstract class BaseTest {

    protected static DataSource dataSource;

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
