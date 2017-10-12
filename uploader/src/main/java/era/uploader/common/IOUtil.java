package era.uploader.common;

import java.io.File;
import java.util.regex.Matcher;

/**
 *
 */
public class IOUtil {
    public static String convertToLocal(String path) {
        return path.replace("\\|/", Matcher.quoteReplacement(File.separator));
    }
}
