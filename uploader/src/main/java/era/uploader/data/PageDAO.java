package era.uploader.data;

import com.google.common.annotations.VisibleForTesting;
import era.uploader.data.model.Page;

import java.util.Collection;
import java.util.Set;

public interface PageDAO {
    void insert(Page page);
    void insertAll(Collection<Page> pages);
    @VisibleForTesting
    Set<Page> getDb();
}
