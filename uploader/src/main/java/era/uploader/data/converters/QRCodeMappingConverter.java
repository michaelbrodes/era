package era.uploader.data.converters;

import com.google.common.base.Converter;
import era.uploader.data.database.jooq.tables.records.QrCodeMappingRecord;
import era.uploader.data.model.QRCodeMapping;
import org.jooq.RecordMapper;

import javax.annotation.Nonnull;

public class QRCodeMappingConverter
        extends Converter<QrCodeMappingRecord, QRCodeMapping>
        implements RecordMapper<QrCodeMappingRecord, QRCodeMapping> {
    public static final QRCodeMappingConverter INSTANCE = new QRCodeMappingConverter();

    private QRCodeMappingConverter() {
        // no op
    }

    @Override
    protected final QRCodeMapping doForward(@Nonnull QrCodeMappingRecord qrCodeMappingRecord) {
        return QRCodeMapping.builder().withStudentId(qrCodeMappingRecord.getStudentId())
                .withSequenceNumber(qrCodeMappingRecord.getSequenceNumber())
                .create(qrCodeMappingRecord.getUuid());
    }

    @Override
    protected final QrCodeMappingRecord doBackward(@Nonnull QRCodeMapping qrCodeMapping) {
        QrCodeMappingRecord record = new QrCodeMappingRecord();
        record.setSequenceNumber(qrCodeMapping.getSequenceNumber());
        record.setUuid(qrCodeMapping.getUuid());
        record.setStudentId(
                qrCodeMapping.getStudent() == null ?
                        null :
                        qrCodeMapping.getStudent().getUniqueId()
        );
        return record;
    }

    @Override
    public QRCodeMapping map(QrCodeMappingRecord record) {
        return convert(record);
    }
}
