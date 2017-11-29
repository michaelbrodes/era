package era.server.common;

/**
 * Singleton object that holds all the application specific configurations
 * that should be supplied on startup. For instance launching the jar with the
 * follow parameters will set the connection string to
 * jdbc:mariadb://localhost:3001/era?user=root&password=password :
 * <code>
 *     --host     localhost
 *     --port     3001
 *     --user     root
 *     --password password
 * </code>
 */
public class AppConfig {
    private static AppConfig INSTANCE;
    private static final String DB_NAME = "era";
    private String connectionString;

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
     * Creates a connection string from the host, port, user, and password
     * information supplied by the command line.
     *
     * @param host the url where the access should be located
     * @param port the port where the access application lives
     * @param user the username we should use to connect to the access
     * @param password the password for the username from the previous arg
     */
    public void setConnectionString(
            String host,
            String port,
            String user,
            String password
    ) {
        this.connectionString =
                "jdbc:mariadb://"
                +  host
                + ":"
                + port
                + "/"
                + DB_NAME
                + "?user="
                + user
                + "&password="
                + password;
    }

    public String getConnectionString() {
        return connectionString;
    }
}
