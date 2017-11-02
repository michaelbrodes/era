package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.PageDAO;
import era.uploader.data.model.QRCodeMapping;

import java.util.Collection;
import java.util.Set;

/**
 * Provides CRUD functionality for Pages inside a database.
 */
public class PageDAOImpl implements PageDAO {
    private final Set<QRCodeMapping> db = Sets.newHashSet();

    @Override
    public void insert(QRCodeMapping QRCodeMapping) {
        if (!getDb().add(QRCodeMapping)) {
            throw new IllegalArgumentException("QRCodeMapping wasn't unique");
        }
    }

    @Override
    public void insertAll(Collection<QRCodeMapping> QRCodeMappings) {
        for (QRCodeMapping QRCodeMapping : QRCodeMappings) {
            insert(QRCodeMapping);
        }
    }

    @Override
    public QRCodeMapping read(String uuid) {
        for  (QRCodeMapping QRCodeMapping : db) {
            if (QRCodeMapping.getUuid().equals(uuid)) {
                return QRCodeMapping;
            }
        }
        return null;
    }

    public Set<QRCodeMapping> getDb () {
        return db;
    }
}
