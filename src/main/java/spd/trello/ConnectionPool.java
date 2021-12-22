package spd.trello;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {

    private static DataSource dataSource;

    public static Connection getConnection() throws IOException, SQLException {
        if(dataSource == null){
            dataSource = createDataSource();
        }
        return dataSource.getConnection();
    }

    public static DataSource createDataSource() throws IOException {
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

    private static Properties loadProperties() throws IOException {
        InputStream inputStream = ConnectionPool.class.getClassLoader()
                .getResourceAsStream("database.properties");

        Properties result = new Properties();
        result.load(inputStream);

        return result;
    }
}
