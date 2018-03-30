package era.server.common;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Singleton object that holds all the application specific configurations
 * that should be supplied on startup. For instance launching the jar with the
 * follow parameters will set the connection string to
 * jdbc:mariadb://localhost:3001/era?dbUser=root&password=password :
 * <code>
 *     --host     localhost
 *     --port     3001
 *     --dbUser     root
 *     --password password
 * </code>
 */
@ParametersAreNonnullByDefault
public class AppConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
    private static final String JDBC_PREFIX = "jdbc:mariadb://";
    private static AppConfig INSTANCE;
    private String connectionString;
    private int port;
    private String dbName;
    private String host;
    private String dbPort;
    private String dbUser;
    private String password;
    private boolean casEnabled;

    private AppConfig() {
    }

    public static AppConfig instance() {
        if (INSTANCE == null) {
            synchronized (AppConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppConfig();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * Creates a connection string from the host, port, dbUser, and password
     * information supplied by the command line.
     *
     * @param host the url where the access should be located
     * @param port the port where the access application lives
     * @param user the username we should use to connect to the access
     * @param password the password for the username from the previous arg
     */
    public void setConnectionString(
            String dbName,
            String host,
            String port,
            String user,
            String password
    ) {
        this.dbName = dbName;
        this.host = host;
        this.dbPort = port;
        this.dbUser = user;
        this.password = password;
        this.connectionString =
                JDBC_PREFIX
                +  host
                + ":"
                + port
                + "/"
                + dbName
                + "?user="
                + user
                + "&password="
                + password;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getConnectionURL () {
        if (host == null || dbPort == null || dbName == null) {
            return null;
        } else {
            return JDBC_PREFIX + host + ":" + dbPort + "/" + dbName;
        }
    }

    public void setPort(String port) {
        Preconditions.checkNotNull(port);
        int portNum;
        try {
            portNum = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            LOGGER.warn("Non-numeric port number, defaulting to 80");
            portNum = 80;
        }
        LOGGER.info("Setting port to {}", portNum);
        Spark.port(portNum);
        this.port = portNum;
    }

    public int getPort() {
        return port;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return password;
    }

    public boolean isCASEnabled() {
        return casEnabled;
    }

    public void setCASEnabled(Boolean casEnabled) {
        // checking if equal to true prevents null pointer exceptions if casEnabled is null
        this.casEnabled = Boolean.TRUE.equals(casEnabled);
    }
}
