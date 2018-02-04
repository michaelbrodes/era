package era.server.data;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool implements ConnectionProvider{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);
    private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/era";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";
    private static ConnectionPool INSTANCE;
    private final BasicDataSource dataSource;

    private ConnectionPool() {
        Properties serverProperties = readServerProperties();
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl(
                serverProperties.getProperty("db.url", DEFAULT_URL)
        );
        dataSource.setUsername(
                serverProperties.getProperty("db.user", DEFAULT_USER)
        );
        dataSource.setPassword(
                serverProperties.getProperty("db.password", DEFAULT_PASS)
        );
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setTimeBetweenEvictionRunsMillis(10000);
        LOGGER.info("Connection pool started.");
    }

    private Properties readServerProperties() {
        Properties properties = new Properties();
        try {
            InputStream propsFile = Files.newInputStream(
                    Paths.get("server.properties"),
                    StandardOpenOption.READ
            );
            properties.load(propsFile);
        } catch (IOException e) {
            LOGGER.error("Couldn't open server properties file, we need to die");
            System.exit(1);
        }

        return properties;
    }

    @Override
    public Connection acquire() throws DataAccessException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Cannot connect to data source");
            System.exit(1);
            return null;
        }
    }

    @Override
    public void release(Connection connection) throws DataAccessException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    public static ConnectionPool instance() {
        if (INSTANCE == null) {
            synchronized (ConnectionPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionPool();
                }
            }
        }

        return INSTANCE;
    }
}
