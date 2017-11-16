package era.uploader.data.database;

import era.uploader.data.DAO;
import era.uploader.data.converters.AssignmentConverter;
import era.uploader.data.converters.QRCodeMappingConverter;
import era.uploader.data.converters.StudentConverter;
import era.uploader.data.database.jooq.tables.records.AssignmentRecord;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.database.jooq.tables.records.QrCodeMappingRecord;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultRecordMapper;
import org.sqlite.SQLiteDataSource;

import javax.annotation.Nullable;
import javax.sql.DataSource;

/**
 * This the direct parent class of all {@link DAO}s in the database
 * package. Each DAO should provide conversion from the JOOQ Record
 * object and the corresponding model it is providing CRUD for.
 *
 * Most of the conversion logic should be done by classes in the
 * {@link era.uploader.data.converters} package.
 */
abstract class DatabaseDAO<RECORD extends Record, MODEL> implements DAO {
    private static final ConnectionPool POOL = ConnectionPool.instance();
    private static final DataSource SOURCE;
    static {
        SQLiteDataSource source = new SQLiteDataSource();
        source.setUrl(ConnectionPool.getDBUrl());
        SOURCE = source;
    }

    /**
     * Converts a generic {@link Record} object to a generic POJO from the
     * {@link era.uploader.data.model} package. Null will be converted to null.
     * Most of the logic will be done in a
     * {@link com.google.common.base.Converter#doForward(Object)} of a
     * corresponding converter object in the
     * {@link era.uploader.data.converters} package.
     *
     * @param record a nullable {@link Record} object.
     * @return a nullable POJO from {@link era.uploader.data.model} that
     * corresponds to the inputted RECORD
     */
    @Nullable
    public abstract MODEL convertToModel(@Nullable RECORD record);

    /**
     * Converts a generic model object from {@link era.uploader.data.model} to
     * a generic JOOQ {@link Record}. Null will be converted to null.
     * Most of the logic will be done in a
     * {@link com.google.common.base.Converter#doBackward(Object)} of a
     * corresponding converter object in the
     * {@link era.uploader.data.converters} package.
     *
     * @param model a nullable POJO from {@link era.uploader.data.model}.
     * @return a nullable JOOQ record.
     */
    @Nullable
    public abstract RECORD convertToRecord(@Nullable MODEL model);

    /**
     * Creates a new {@link DSLContext} with a configuration that uses
     * {@link ConnectionPool} as its {@link ConnectionProvider}, SQLite as its
     * {@link SQLDialect}, and converters in the
     * {@link era.uploader.data.converters} package as its record mapper
     */
    public DSLContext connect() {
        return DSL.using(
                new DefaultConfiguration()
                    .set(SOURCE)
                    .set(POOL)
                    .set(new RecordMapperProvider() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {
                            if (type == Assignment.class && recordType instanceof AssignmentRecord) {
                                RecordMapper<AssignmentRecord, Assignment> converter = AssignmentConverter.INSTANCE;
                                return (RecordMapper<R, E>) converter;
                            } else if (type == Student.class && recordType instanceof StudentRecord) {
                                RecordMapper<StudentRecord, Student> converter = StudentConverter.INSTANCE;
                                return (RecordMapper<R, E>) converter;
                            } else if (type == Course.class && recordType instanceof CourseRecord) {
                                RecordMapper<StudentRecord, Student> converter = StudentConverter.INSTANCE;
                                return (RecordMapper<R, E>) converter;
                            } else if (type == QRCodeMapping.class && recordType instanceof QrCodeMappingRecord){
                                RecordMapper<QrCodeMappingRecord, QRCodeMapping> converter = QRCodeMappingConverter.INSTANCE;
                                return (RecordMapper<R, E>) converter;
                            } else {
                                return new DefaultRecordMapper<>(recordType, type);
                            }
                        }
                    })
        );
    }
}
