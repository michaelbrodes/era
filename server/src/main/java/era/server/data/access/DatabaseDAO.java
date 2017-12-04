package era.server.data.access;

import era.server.data.ConnectionPool;
import era.server.data.DAO;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 * This the direct parent class of all {@link DAO}s in the access
 * package. Each DAO should provide conversion from the JOOQ Record
 * object and the corresponding model it is providing CRUD for.
 */
abstract class DatabaseDAO implements DAO {
    private static final ConnectionPool POOL = ConnectionPool.instance();

    /**
     * Creates a new {@link DSLContext} with a configuration that uses
     * {@link ConnectionPool} as its {@link ConnectionProvider}.
     */
    @SuppressWarnings("WeakerAccess")
    protected DSLContext connect() {
        return DSL.using(
                new DefaultConfiguration()
                        .set(POOL)
        );
    }
}

