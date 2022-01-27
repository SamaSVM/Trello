package spd.trello.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:database.properties")
@ComponentScan(basePackages = {"spd.trello.services", "spd.trello.repository"})
public class Config implements InitializingBean {
    @Value ("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    @Value("${jdbc.pool.max}")
    private String poolMax;
    @Value("${jdbc.driver.name}")
    private String driverName;

    @Bean
    public DataSource dataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverName);
        int maxPool = Integer.parseInt(poolMax);
        config.setMaximumPoolSize(maxPool);
        return new HikariDataSource(config);
    }

    @Override
    public void afterPropertiesSet() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource())
                .load();
        flyway.migrate();
    }
}
