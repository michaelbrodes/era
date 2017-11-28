package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.QRCodeMapping;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

/**
 * This class is mock of {@link QRCodeMappingDAOImpl} to make unit testing methods
 * using CRUD functionality easier - you don't have to generate a new SQLite
 * database per each test. <strong>This is not a unit test for
 * {@link QRCodeMappingDAOImpl}</strong>. You should test the that DAO against the
 * real database using a corresponding integration test.
 */
public class MockQRCodeMappingDAOImpl implements QRCodeMappingDAO, MockDAO<QRCodeMapping> {
    private final Set<QRCodeMapping> db = Sets.newHashSet();

    @Override
    public void insert(@Nonnull QRCodeMapping QRCodeMapping) {
        if (!getDb().add(QRCodeMapping)) {
            throw new IllegalArgumentException("QRCodeMapping wasn't unique");
        }
    }

    @Override
    public void insertAll(@Nonnull Collection<QRCodeMapping> QRCodeMappings) {
        for (QRCodeMapping QRCodeMapping : QRCodeMappings) {
            insert(QRCodeMapping);
        }
    }

    @Override
    public void update(@Nonnull QRCodeMapping changedQRCodeMapping) {
        QRCodeMapping prevQRMapping = read(changedQRCodeMapping.getUuid());
        db.remove(prevQRMapping);
        db.add(changedQRCodeMapping);
    }

    @Override
    public QRCodeMapping read(@Nonnull String uuid) {
        return db.stream()
                .filter(page -> page.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void delete(String uuid) {
        QRCodeMapping qr = db.stream()
                .filter(qrCodeMapping -> qrCodeMapping.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
        db.remove(qr);
    }

    @Override
    public Set<QRCodeMapping> getDb () {
        return db;
    }


}
