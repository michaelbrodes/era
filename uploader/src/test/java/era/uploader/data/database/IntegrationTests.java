package era.uploader.data.database;

import era.uploader.data.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class for running database integration tests.
 *
 * ALL THESE METHODS ARE VERY SPOOKY AND SCARY AND IN THE WRONG HANDS
 * DISASTROUS. USE WITH CAUTION.
 */
class IntegrationTests {
    /**
     * Connects to the database and clears all information in that database
     * besides the schema
     */
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

    /**
     * Deletes all information in a table and resets it's AUTOINCREMENT column
     * to 0
     */
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

    /**
     * This will just run an arbitrary SQL script against the connection
     */
    static void applySqlScript(String scriptPath, Connection connection) {
        throw new UnsupportedOperationException();
    }
}
