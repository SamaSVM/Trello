package spd.trello;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spd.trello.configuration.Config;

import javax.sql.DataSource;

public abstract class BaseTest {
    protected static DataSource dataSource;
    protected static AnnotationConfigApplicationContext context;

    @BeforeAll
    public static void init() {
        context = new AnnotationConfigApplicationContext(Config.class);
        dataSource = context.getBean(DataSource.class);
    }
}
