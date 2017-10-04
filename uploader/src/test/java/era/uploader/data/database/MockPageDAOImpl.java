package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.PageDAO;
import era.uploader.data.model.Page;

import java.util.Collection;
import java.util.Set;

/**
 * This class is mock of {@link PageDAOImpl} to make unit testing methods
 * using CRUD functionality easier - you don't have to generate a new SQLite
 * database per each test. <strong>This is not a unit test for
 * {@link PageDAOImpl}</strong>. You should test the that DAO against the
 * real database using a corresponding integration test.
 */
public class MockPageDAOImpl implements PageDAO, MockDAO<Page> {
    private final Set<Page> db = Sets.newHashSet();

    @Override
    public void insert(Page page) {
        if (!getDb().add(page)) {
            throw new IllegalArgumentException("Page wasn't unique");
        }
    }

    @Override
    public void insertAll(Collection<Page> pages) {
        for (Page page : pages) {
            insert(page);
        }
    }

    @Override
    public Set<Page> getDb () {
        return db;
    }
}
