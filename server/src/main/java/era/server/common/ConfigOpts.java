package era.server.common;

import java.util.Map;

/**
 * All command line arguments that can be used to configure how the Server
 * runs.
 */
public enum ConfigOpts {
    SERVER_PORT,
    DB_NAME,
    DB_PORT,
    HOST,
    USER,
    PASSWORD,
    CAS_ENABLED;

    // here to make it easier to add new options per new enum constants
    static final String SERVER_PORT_OPT = "--app-port";
    static final String DB_PORT_OPT = "--db-port";
    static final String DB_NAME_OPT = "--db-name";
    static final String HOST_OPT = "--db-host";
    static final String USER_OPT = "--db-user";
    static final String PASSWORD_OPT = "--db-password";
    static final String CAS_ENABLED_OPT = "--cas-enabled";

    /**
     * Parse arguments that have come in through the command line into a map
     * of option to value. This allows for effective query of of whether an
     * option is set.
     *
     * @param args an array of arguments sent in through the command line
     * @return config options to the values that they map via the supplied
     * arguments
     */
    public static Map<ConfigOpts, String> parseArgs (String[] args) {
        ConfigParser parser = new ConfigParser(args);
        return parser.parseArgs();
    }

}
