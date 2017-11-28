package era.uploader.data;

import era.uploader.data.model.QRCodeMapping;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public interface QRCodeMappingDAO extends DAO {
    void insert(QRCodeMapping QRCodeMapping);
    void insertAll(Collection<QRCodeMapping> QRCodeMappings);
    void update(QRCodeMapping changedQRCodeMapping);
    @Nullable
    QRCodeMapping read(String uuid);
}
