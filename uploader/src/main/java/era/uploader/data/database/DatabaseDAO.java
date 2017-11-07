package era.uploader.data.database;

import era.uploader.data.DAO;
import org.jooq.DSLContext;
import org.jooq.Record;

/**
 * This the direct parent interface of all {@link DAO}s in the database
 * package. Each DAO should provide conversion from the JOOQ Record
 * object and the corresponding model it is providing CRUD for.
 */
public interface DatabaseDAO<R extends Record, M> {
    M convertToModel(R record);
    R convertToRecord(M model, DSLContext ctx);
}
