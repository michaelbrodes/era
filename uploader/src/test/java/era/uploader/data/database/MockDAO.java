package era.uploader.data.database;

import era.uploader.data.model.Student;

import java.util.Set;

/**
 * A mock DAO is a Data Access Object not linked to a real Database. It is
 * there to make unit testing classes that make use of CRUD much easier - you
 * don't have to generate a new SQLite database per unit test. It should
 * store it's objects into a {@link java.util.Set}.
 */
public interface MockDAO<T> {
    Set<T> getDb();
}
