package era.server.common;

import java.io.File;
import java.util.regex.Matcher;

/**
 * A bunch of common utility methods for file I/O
 */
public class IOUtil {
    /**
     * We are developing on both MacOS and Windows so we can't have either "\"
     * or "/" in our paths. This methods changes those slashes to the correct
     * one for this operating system. ONLY USE THIS METHOD DURING TESTS.
     * Paths in production should use File.separator rather than any slashes
     *
     * @param path the path we need to convert slashes
     * @return a path containing operating system specific slashes.
     */
    public static String convertToLocal(String path) {
        return path.replace("\\|/", Matcher.quoteReplacement(File.separator));
    }

    public static String removeSpaces(String path) {
        return path.replace("\\s", "-");
    }
}

