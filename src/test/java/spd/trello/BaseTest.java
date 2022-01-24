package spd.trello;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spd.trello.configuration.Config;
import spd.trello.db.ConnectionPool;

public abstract class BaseTest {
    protected static HikariDataSource dataSource;
    protected static AnnotationConfigApplicationContext context;

    @BeforeAll
    public static void init() {
        HikariConfig cfg = new HikariConfig();
        cfg.setPassword("postgres");
        cfg.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_UPPER=false");
        cfg.setUsername("postgres");
        cfg.setDriverClassName("org.h2.Driver");
        dataSource = new HikariDataSource(cfg);
        ConnectionPool.setDataSource(dataSource);
        context = new AnnotationConfigApplicationContext(Config.class);
    }
}
