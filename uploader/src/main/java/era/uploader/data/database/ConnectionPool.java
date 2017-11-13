package era.uploader.data.database;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool implements ConnectionProvider {
    private static ConnectionPool INSTANCE;
    private final SQLiteConnectionPoolDataSource delegate;

    private ConnectionPool () {
        delegate = new SQLiteConnectionPoolDataSource();
        String dbUrl = getDBUrl();
        delegate.setUrl(dbUrl);
    }

    public static String getDBUrl() {
        File propFile = new File("uploader.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(propFile));
        } catch (IOException e) {
            System.err.println("Cannot stat " + propFile.getAbsolutePath());
            System.exit(1);
        }

        String dbUrl = properties.getProperty("db.url");
        if (dbUrl == null) {
            System.err.println(propFile.getAbsolutePath()+ " doesn't have a \"db.url\" property");
            System.exit(1);
        }

        Path dbPath = Paths.get(dbUrl);

        if(!Files.exists(dbPath)) {
            System.err.println("Database " + dbUrl + " does not exist");
            System.exit(1);
        }

        if (!JDBC.isValidURL(dbUrl)) {
            dbUrl = JDBC.PREFIX + dbPath.toAbsolutePath().toString();
        }

        return dbUrl;
    }

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

    @Override
    public Connection acquire() throws DataAccessException {
        try {
            return delegate.getPooledConnection().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // fatal so exit
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
            // fatal so exit
            System.exit(1);
        }
    }
}
