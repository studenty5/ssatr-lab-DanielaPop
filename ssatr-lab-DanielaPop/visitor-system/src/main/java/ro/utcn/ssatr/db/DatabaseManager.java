package ro.utcn.ssatr.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/facility_access";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    private static Connection connection;

    public static Connection getConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void initDatabase() {

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS visits (
                    id UUID PRIMARY KEY,
                    visitor_name VARCHAR(100),
                    email VARCHAR(100),
                    host_name VARCHAR(100),
                    visitor_type VARCHAR(50),
                    start_time TIMESTAMP,
                    expiration_time TIMESTAMP,
                    entry_time TIMESTAMP,
                    exit_time TIMESTAMP,
                    status VARCHAR(20)
                )
            """);

            System.out.println("Conectare PostgreSQL reusita. Tabela verificata.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}