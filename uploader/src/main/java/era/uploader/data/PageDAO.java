package era.uploader.data;

import era.uploader.data.model.Page;

import java.util.Collection;

public interface PageDAO extends DAO {
    void insert(Page page);
    void insertAll(Collection<Page> pages);
    Page read(String uuid);
}
