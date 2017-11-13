package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.QRCodeMappingConverter;
import era.uploader.data.database.jooq.tables.records.QrCodeMappingRecord;
import era.uploader.data.model.QRCodeMapping;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.QR_CODE_MAPPING;



/**
 * Provides CRUD functionality for Pages inside a database.
 */
public class QRCodeMappingDAOImpl extends DatabaseDAO<QrCodeMappingRecord, QRCodeMapping> implements QRCodeMappingDAO {
    @Deprecated
    private final Set<QRCodeMapping> db = Sets.newHashSet();
    private static final QRCodeMappingConverter CONVERTER =
            QRCodeMappingConverter.INSTANCE;
    private static QRCodeMappingDAO INSTANCE;

    private QRCodeMappingDAOImpl() {

    }

    @Override
    public void insert(QRCodeMapping QRCodeMapping) {
        try (DSLContext ctx = connect()) {
            QRCodeMapping.setUuid(ctx.insertInto(
                    //table
                    QR_CODE_MAPPING,
                    //columns
                    QR_CODE_MAPPING.SEQUENCE_NUMBER,
                    QR_CODE_MAPPING.STUDENT_ID
            )
            .values(
                    QRCodeMapping.getSequenceNumber(),
                    QRCodeMapping.getStudent().getUniqueId()
            )
            .returning (
                    QR_CODE_MAPPING.UUID
            )
            .fetchOne()
            .getUuid()
            );
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
        try (DSLContext ctx = connect()) {
            ctx.update(QR_CODE_MAPPING)
                    .set(QR_CODE_MAPPING.SEQUENCE_NUMBER, changedQRCodeMapping.getSequenceNumber())
                    .set(QR_CODE_MAPPING.STUDENT_ID, changedQRCodeMapping.getStudent().getUniqueId())
                    .set(QR_CODE_MAPPING.UUID, changedQRCodeMapping.getUuid())
                    .execute();
        }
    }

    public void delete(String uuid) {
        try (DSLContext ctx = connect()) {
            ctx.deleteFrom(QR_CODE_MAPPING)
                    .where(QR_CODE_MAPPING.UUID.eq(uuid))
                    .execute();
        }
    }

    @Override
    public QRCodeMapping convertToModel(QrCodeMappingRecord record) {
        Optional<QRCodeMapping> newQRCodeMapping = Optional
                .ofNullable(CONVERTER.convert(record));
        final StudentDAO studentDAO = StudentDAOImpl.instance();
        newQRCodeMapping.ifPresent((mapping) -> {
            mapping.setStudent(
                studentDAO.fromQRMapping(mapping)
            );
        });
        return newQRCodeMapping.orElse(null);
    }

    @Override
    public QrCodeMappingRecord convertToRecord(QRCodeMapping model, DSLContext ctx) {
        QrCodeMappingRecord qrCodeMapping = ctx.newRecord(QR_CODE_MAPPING);
        qrCodeMapping.setSequenceNumber(model.getSequenceNumber());
        qrCodeMapping.setStudentId(model.getStudent().getUniqueId());

        if (model.getUuid().equals("")) {
            qrCodeMapping.setUuid(model.getUuid());
        }

        // unique id cannot be 0 because that is an invalid database id
        return qrCodeMapping;
    }

    @Override
    public QrCodeMappingRecord convertToRecord(QRCodeMapping qrCodeMapping) {
        return CONVERTER.reverse().convert(qrCodeMapping);
    }

    public static QRCodeMappingDAO instance() {
        if (INSTANCE == null) {
            synchronized (QRCodeMappingDAOImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QRCodeMappingDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}
