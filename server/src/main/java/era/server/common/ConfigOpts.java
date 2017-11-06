package era.server.common;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * All command line arguments that can be used to configure how the Server
 * runs.
 */
public enum ConfigOpts {
    HOST,
    PORT,
    USER,
    PASSWORD;

    private static final String PORT_OPT = "--port";
    private static final String HOST_OPT = "--host";
    private static final String USER_OPT = "--user";
    private static final String PASSWORD_OPT = "--password";

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
        Map<ConfigOpts, String> options = Maps.newHashMap();
        for (int i = 0; i < args.length; i+=2) {
            switch(args[i]) {
                case PORT_OPT:
                    if (i + 1 < args.length) {
                        options.put(ConfigOpts.PORT, args[i +1]);
                    }
                    break;
                case HOST_OPT:
                    if (i + 1 < args.length) {
                        options.put(ConfigOpts.HOST, args[i +1]);
                    }
                    break;
                case USER_OPT:
                    if (i + 1 < args.length) {
                        options.put(ConfigOpts.USER, args[i + 1]);
                    }
                    break;
                case PASSWORD_OPT:
                    if (i + 1 < args.length) {
                        options.put(ConfigOpts.PASSWORD, args[i + 1]);
                    }
                    break;
                default:
                    // NO-OP as there is no option to parse.
                    break;
            }
        }

        return options;
    }
}
