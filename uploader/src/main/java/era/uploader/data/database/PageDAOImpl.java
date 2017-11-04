package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.PageDAO;
import era.uploader.data.model.QRCodeMapping;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.QR_CODE_MAPPING;

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

    /* Modify data stored in already existing QR_CODE_MAPPING in database */
    public void update(QRCodeMapping changedQRCodeMapping) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            ctx.update(QR_CODE_MAPPING)
                    .set(QR_CODE_MAPPING.SEQUENCE_NUMBER, changedQRCodeMapping.getSequenceNumber())
                    .set(QR_CODE_MAPPING.STUDENT_ID, changedQRCodeMapping.getStudent().getUniqueId())
                    .set(QR_CODE_MAPPING.UUID, changedQRCodeMapping.getUuid())
                    .execute();
        }
    }

    public Set<QRCodeMapping> getDb () {
        return db;
    }
}
