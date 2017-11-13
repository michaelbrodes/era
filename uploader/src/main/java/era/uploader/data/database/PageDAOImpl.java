package era.uploader.data.database;

import com.google.common.collect.Sets;
import com.google.zxing.qrcode.encoder.QRCode;
import era.uploader.data.PageDAO;
import era.uploader.data.database.jooq.tables.records.QrCodeMappingRecord;
import era.uploader.data.model.QRCodeMapping;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.QR_CODE_MAPPING;

/**
 * Provides CRUD functionality for Pages inside a database.
 */
public class PageDAOImpl implements PageDAO,  DatabaseDAO<QrCodeMappingRecord, QRCodeMapping> {
    private final Set<QRCodeMapping> db = Sets.newHashSet();

    @Override
    public void insert(QRCodeMapping QRCodeMapping) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
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
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            QrCodeMappingRecord qrCodeMapping = ctx.selectFrom(QR_CODE_MAPPING)
                    .where(QR_CODE_MAPPING.UUID.eq(uuid))
                    .fetchOne();

            return convertToModel(qrCodeMapping);
        }
    }

    public void delete(String uuid) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            ctx.deleteFrom(QR_CODE_MAPPING)
                    .where(QR_CODE_MAPPING.UUID.eq(uuid))
                    .execute();
        }
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

    @Override
    public QRCodeMapping convertToModel(QrCodeMappingRecord record) {
        QRCodeMapping newQRCodeMapping = new QRCodeMapping(
                record.getUuid(),
                record.getSequenceNumber()
        );
        return newQRCodeMapping;
    }

    @Override
    public QrCodeMappingRecord convertToRecord(QRCodeMapping model, DSLContext ctx) {
        QrCodeMappingRecord qrCodeMapping = ctx.newRecord(QR_CODE_MAPPING);
        qrCodeMapping.setSequenceNumber(model.getSequenceNumber());
        qrCodeMapping.setStudentId(model.getStudent().getUniqueId());

        if (model.getUuid() != "") {
            qrCodeMapping.setUuid(model.getUuid());
        }

        // unique id cannot be 0 because that is an invalid database id
        return qrCodeMapping;
    }

}
