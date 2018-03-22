package era.server.common;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A parser utility class for {@link ConfigOpts}. It loops through a main
 * argument array and puts ConfigOpts into a map to their value.
 */
class ConfigParser {
    private final Map<ConfigOpts, String> configMap = Maps.newHashMap();
    private final String[] allArgs;

    ConfigParser(String[] allArgs) {
        this.allArgs = allArgs;
    }

    Map<ConfigOpts, String> parseArgs() {
        for (int i = 0; i < allArgs.length; i+=2) {
            switch(allArgs[i]) {
                case ConfigOpts.SERVER_PORT_OPT:
                    putArg(ConfigOpts.SERVER_PORT, i);
                    break;
                case ConfigOpts.DB_NAME_OPT:
                    putArg(ConfigOpts.DB_NAME, i);
                    break;
                case ConfigOpts.DB_PORT_OPT:
                    putArg(ConfigOpts.DB_PORT, i);
                    break;
                case ConfigOpts.HOST_OPT:
                    putArg(ConfigOpts.HOST, i);
                    break;
                case ConfigOpts.USER_OPT:
                    putArg(ConfigOpts.USER, i);
                    break;
                case ConfigOpts.PASSWORD_OPT:
                    putArg(ConfigOpts.PASSWORD, i);
                    break;
                case ConfigOpts.CAS_ENABLED_OPT:
                    putArg(ConfigOpts.CAS_ENABLED, i);
                default:
                    // NO-OP as there is no option to parse.
                    break;
            }
        }

        return configMap;
    }

    private void putArg(
            ConfigOpts option,
            int allArgsIteration
    ) {
        if (allArgsIteration + 1 < allArgs.length) {
            configMap.put(option, allArgs[allArgsIteration + 1]);
        } else if (option == ConfigOpts.PASSWORD) {
            configMap.put(option, "");
        }
        else if (option == ConfigOpts.PASSWORD) {
            configMap.put(option, "");
        }
    }
}
