package era.server.data.database;

import com.google.common.collect.Sets;
import era.server.data.model.Page;


import java.util.Collection;
import java.util.Set;

/**
 * Provides CRUD functionality for Pages inside a database.
 */
public class PageDAO {
    private final Set<Page> db = Sets.newHashSet();


    public void insert(Page page) {
        if (!getDb().add(page)) {
            throw new IllegalArgumentException("Page wasn't unique");
        }
    }

    public void insertAll(Collection<Page> pages) {
        for (Page page : pages) {
            insert(page);
        }
    }

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
