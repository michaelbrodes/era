package era.uploader.data.database;

import com.google.common.base.Preconditions;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.QRCodeMappingConverter;
import era.uploader.data.database.jooq.tables.records.QrCodeMappingRecord;
import era.uploader.data.model.QRCodeMapping;
import org.jooq.DSLContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;

import static era.uploader.data.database.jooq.Tables.QR_CODE_MAPPING;

/**
 * Provides CRUD functionality for Pages inside a database.
 */
@ParametersAreNonnullByDefault
public class QRCodeMappingDAOImpl extends DatabaseDAO<QrCodeMappingRecord, QRCodeMapping> implements QRCodeMappingDAO {
    private static final QRCodeMappingConverter CONVERTER =
            QRCodeMappingConverter.INSTANCE;
    private static QRCodeMappingDAO INSTANCE;

    private QRCodeMappingDAOImpl() {

    }

    @Override
    public void insert(@Nonnull QRCodeMapping QRCodeMapping) {
        Preconditions.checkNotNull(QRCodeMapping, "cannot insert null qrCodeMapping");
        try (DSLContext ctx = connect()) {
           ctx.insertInto(
                    //table
                    QR_CODE_MAPPING,
                    //columns
                    QR_CODE_MAPPING.SEQUENCE_NUMBER,
                    QR_CODE_MAPPING.STUDENT_ID,
                    QR_CODE_MAPPING.UUID
            )
            .values(
                    QRCodeMapping.getSequenceNumber(),
                    QRCodeMapping.getStudent().getUniqueId(),
                    QRCodeMapping.getUuid()
            )
            .execute();
        }
    }

    @Override
    public void insertAll(@Nonnull Collection<QRCodeMapping> QRCodeMappings) {
        Preconditions.checkNotNull(QRCodeMappings, "cannot insert null qrCodeMappings");
        for (QRCodeMapping QRCodeMapping : QRCodeMappings) {
            insert(QRCodeMapping);
        }
    }

    @Override
    @Nullable
    public QRCodeMapping read(@Nonnull String uuid) {
        Preconditions.checkNotNull(uuid, "cannot read null uuid");
        try (DSLContext ctx = connect()) {
            QrCodeMappingRecord qrCodeMapping = ctx.selectFrom(QR_CODE_MAPPING)
                    .where(QR_CODE_MAPPING.UUID.eq(uuid))
                    .fetchOne();

            return convertToModel(qrCodeMapping);
        }
    }

    public void delete(String uuid) {
        Preconditions.checkNotNull(uuid, "cannot read a null uuid");
        try (DSLContext ctx = connect()) {
            ctx.deleteFrom(QR_CODE_MAPPING)
                    .where(QR_CODE_MAPPING.UUID.eq(uuid))
                    .execute();
        }
    }

    /* Modify data stored in already existing QR_CODE_MAPPING in database */
    public void update(@Nonnull QRCodeMapping changedQRCodeMapping) {
        Preconditions.checkNotNull(changedQRCodeMapping, "Cannot update a null changedQrCodeMapping");
        try (DSLContext ctx = connect()) {
            ctx.update(QR_CODE_MAPPING)
                    .set(QR_CODE_MAPPING.SEQUENCE_NUMBER, changedQRCodeMapping.getSequenceNumber())
                    .set(QR_CODE_MAPPING.STUDENT_ID, changedQRCodeMapping.getStudent().getUniqueId())
                    .set(QR_CODE_MAPPING.UUID, changedQRCodeMapping.getUuid())
                    .execute();
        }
    }

    @Override
    @Nullable
    public QRCodeMapping convertToModel(@Nullable QrCodeMappingRecord record) {
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
    @Nullable
    public QrCodeMappingRecord convertToRecord(@Nullable QRCodeMapping qrCodeMapping) {
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
