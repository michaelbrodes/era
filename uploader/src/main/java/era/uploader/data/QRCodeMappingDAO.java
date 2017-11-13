package era.uploader.data;

import era.uploader.data.model.QRCodeMapping;

import java.util.Collection;

public interface QRCodeMappingDAO extends DAO {
    void insert(QRCodeMapping QRCodeMapping);
    void insertAll(Collection<QRCodeMapping> QRCodeMappings);
    void update(QRCodeMapping changedQRCodeMapping);
    QRCodeMapping read(String uuid);
}
