package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.PageDAO;
import era.uploader.data.model.QRCodeMapping;

import java.util.Collection;
import java.util.Set;

/**
 * This class is mock of {@link PageDAOImpl} to make unit testing methods
 * using CRUD functionality easier - you don't have to generate a new SQLite
 * database per each test. <strong>This is not a unit test for
 * {@link PageDAOImpl}</strong>. You should test the that DAO against the
 * real database using a corresponding integration test.
 */
public class MockPageDAOImpl implements PageDAO, MockDAO<QRCodeMapping> {
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
    public void update(QRCodeMapping changedQRCodeMapping) {
        QRCodeMapping prevQRMapping = read(changedQRCodeMapping.getUuid());
        db.remove(prevQRMapping);
        db.add(changedQRCodeMapping);
    }

    @Override
    public QRCodeMapping read(String uuid) {
        return db.stream()
                .filter(page -> page.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<QRCodeMapping> getDb () {
        return db;
    }


}
