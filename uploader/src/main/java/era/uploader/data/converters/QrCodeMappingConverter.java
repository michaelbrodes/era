package era.uploader.data.converters;

import com.google.common.base.Converter;
import era.uploader.data.database.jooq.tables.records.QrCodeMappingRecord;
import era.uploader.data.model.QRCodeMapping;

import javax.annotation.Nonnull;

public class QrCodeMappingConverter extends Converter<QrCodeMappingRecord, QRCodeMapping> {
    @Override
    protected QRCodeMapping doForward(@Nonnull QrCodeMappingRecord qrCodeMappingRecord) {
        return QRCodeMapping.builder()
                .withSequenceNumber(qrCodeMappingRecord.getSequenceNumber())
                .create(qrCodeMappingRecord.getUuid());
    }

    @Override
    protected QrCodeMappingRecord doBackward(@Nonnull QRCodeMapping qrCodeMapping) {
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
}
