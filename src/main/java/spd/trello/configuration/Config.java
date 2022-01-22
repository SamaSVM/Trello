package spd.trello.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import spd.trello.db.ConnectionPool;
import spd.trello.db.FlywayMigrate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"spd.trello.services", "spd.trello.repository"})
public class Config {

    @Bean
    public DataSource dataSource(){
        FlywayMigrate.doMigrate();
        return ConnectionPool.createDataSource();
    }
}
