package sagrada.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final static String JDBC_URL = "jdbc:mysql://databases.aii.avans.nl/";
    private final static String JDBC_DB = "1920_vsoprj2_vtf";
    private final static String JDBC_USERNAME = "1920_vsoprj2_vtf";
    private final static String JDBC_PASSWORD = "X6tGzVN55VjhkaiC";

    private Connection connection;

    public void connect() throws SQLException {
        this.connection = DriverManager.getConnection(JDBC_URL + JDBC_DB, JDBC_USERNAME, JDBC_PASSWORD);
    }

    public Connection getConnection() {
        return this.connection;
    }
}
