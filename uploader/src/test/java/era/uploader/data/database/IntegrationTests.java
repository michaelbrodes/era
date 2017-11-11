package era.uploader.data.database;

import era.uploader.data.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class for running database integration tests.
 */
class IntegrationTests {
    static Connection clearAndConnect(List<String> tablesToClear) {
        // connect to the database using JDBC
        Connection dbConnection = null;
        try {
            dbConnection = DriverManager.getConnection(DAO.CONNECTION_STR);
            clearTables(dbConnection, tablesToClear);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return dbConnection;
    }

    static void clearTables(Connection dbConnection, List<String> tablesToClear) {
        for (String table : tablesToClear) {
            String wipeSql = "DELETE FROM " + table;
            String seqZeroSql = "UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + table + "'";
            try (PreparedStatement wipe = dbConnection.prepareStatement(wipeSql);
                    PreparedStatement seqZero = dbConnection.prepareStatement(seqZeroSql)) {
                // delete all records in a table
                System.out.println (
                        "You are about to delete all records from "
                                + table
                                + " in "
                                + DAO.CONNECTION_STR
                );
                wipe.executeUpdate();
                System.out.println("deleted " + table);
                seqZero.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
