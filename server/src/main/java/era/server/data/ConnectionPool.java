package era.server.data;

import era.server.common.AppConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool implements ConnectionProvider{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);
    private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/era";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";
    private static ConnectionPool INSTANCE;
    private final BasicDataSource dataSource;

    private ConnectionPool() {
        AppConfig serverConfig = AppConfig.instance();

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl(
                serverConfig.getConnectionURL()
        );
        dataSource.setUsername(
                serverConfig.getDbUser()
        );
        dataSource.setPassword(
                serverConfig.getDbPassword()
        );
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setTimeBetweenEvictionRunsMillis(10000);
        LOGGER.info("Connection pool started.");
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
