package spd.trello;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {

    private static HikariDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        if(dataSource == null){
            dataSource = createDataSource();
        }
        return dataSource.getConnection();
    }

    public static HikariDataSource createDataSource() {
        if(dataSource != null){
            return dataSource;
        }
        Properties properties = loadProperties();
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(properties.getProperty("jdbc.url"));
        config.setUsername(properties.getProperty("jdbc.username"));
        config.setPassword(properties.getProperty("jdbc.password"));

        return new HikariDataSource(config);
    }

    private static Properties loadProperties() {
        InputStream inputStream = ConnectionPool.class.getClassLoader()
                .getResourceAsStream("database.properties");

        Properties result = new Properties();
        try {
            result.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Properties not loaded!");
        }

        return result;
    }
}
