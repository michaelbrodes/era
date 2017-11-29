package era.uploader.data.database;

import era.uploader.common.Threads;
import era.uploader.common.UploaderProperties;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.sqlite.JDBC;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Every SQLite database connection will require a file access and setting up
 * SQLite itself. This is rather expensive if you have a lot of queries in
 * close succession. This is a wrapper for
 * {@link SQLiteConnectionPoolDataSource} that implements
 * {@link ConnectionProvider} that pools as few of these database connections
 * as possible.
 */
public class ConnectionPool implements ConnectionProvider {
    private static ConnectionPool INSTANCE;
    private final SQLiteConnectionPoolDataSource delegate;

    private ConnectionPool () {
        delegate = new SQLiteConnectionPoolDataSource();
        String dbUrl = getDBUrl();
        delegate.setUrl(dbUrl);
    }

    public static String getDBUrl() {
        UploaderProperties props = UploaderProperties.instance();
        Optional<String> dbUrl = props.getDbUrl();
        if (!dbUrl.isPresent()) {
            System.err.println(props.getFile().getAbsolutePath()
                    + " doesn't have a \"db.url\" property");
            System.exit(1);
        }

        Path dbPath = Paths.get(dbUrl.get());

        if(!Files.exists(dbPath)) {
            System.err.println("Database " + dbUrl + " does not exist");
            System.exit(1);
        }

        if (!JDBC.isValidURL(dbUrl.get())) {
            dbUrl = Optional.of(JDBC.PREFIX + dbPath.toAbsolutePath().toString());
        }

        return dbUrl.orElse(null);
    }

    public static ConnectionPool instance() {
        INSTANCE = Threads.doubleCheck(
                INSTANCE,
                ConnectionPool::new,
                ConnectionPool.class
        );

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
