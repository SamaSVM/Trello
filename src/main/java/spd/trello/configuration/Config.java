package spd.trello.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import spd.trello.db.ConnectionPool;
import spd.trello.db.FlywayMigrate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"spd.trello.services", "spd.trello.repository"})
public class Config implements InitializingBean {

    @Bean
    public DataSource dataSource(){
        return ConnectionPool.createDataSource();
    }

    @Override
    public void afterPropertiesSet() {
        FlywayMigrate.doMigrate();
    }
}
