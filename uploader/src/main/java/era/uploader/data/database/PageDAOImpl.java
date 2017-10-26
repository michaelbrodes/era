package era.uploader.data.database;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import era.uploader.data.PageDAO;
import era.uploader.data.model.Page;

import java.util.Collection;
import java.util.Set;

/**
 * Provides CRUD functionality for Pages inside a database.
 */
public class PageDAOImpl implements PageDAO {
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
    public Page read(String uuid) {
        for  (Page page: db) {
            if (page.getUuid().equals(uuid)) {
                return page;
            }
        }
        return null;
    }

    public Set<Page> getDb () {
        return db;
    }
}
