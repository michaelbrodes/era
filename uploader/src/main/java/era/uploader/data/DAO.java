package era.uploader.data;

import java.io.File;

/**
 * The generic Data Access Object that all DAOs will implement. A DAO is how
 * we provide object to relational mapping in our application. They will
 * provide methods that interface with the database using either HTTP or
 * {@link org.jooq}
 */
public interface DAO {
    String CONNECTION_STR = "jdbc:sqlite:" + new File("uploader.db").getAbsolutePath();
}
