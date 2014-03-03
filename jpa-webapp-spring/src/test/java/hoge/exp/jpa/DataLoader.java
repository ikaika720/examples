package hoge.exp.jpa;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataLoader {
    private static final int DEFAULT_NUM_OF_RCD = 10_000;
    private static final String DEFAULT_DB_URL = "jdbc:postgresql:mydb";
    private static final String DEFAULT_DB_USER = "user01";
    private static final String DEFAULT_DB_PASSWD = "password";

    public static void main(String[] args) {
        int numOfRecordToInsert = args.length > 1
                ? Integer.parseInt(args[0]) : DEFAULT_NUM_OF_RCD;
        String databaseURL = args.length > 2 ? args[1] : DEFAULT_DB_URL;
        String user = args.length > 3 ? args[2] : DEFAULT_DB_USER;
        String password = args.length > 4 ? args[3] : DEFAULT_DB_PASSWD;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(
                databaseURL, user, password)) {
            conn.setAutoCommit(false);

            conn.createStatement().executeUpdate("TRUNCATE TABLE transaction");
            conn.createStatement().executeUpdate("TRUNCATE TABLE account CASCADE");
            conn.createStatement().executeUpdate("ALTER SEQUENCE transaction_id_seq RESTART WITH 100");

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO account VALUES(?, ?, CURRENT_TIMESTAMP)");
            for (int i = 0; i < numOfRecordToInsert; i++) {
                ps.setLong(1, i);
                ps.setBigDecimal(2, BigDecimal.valueOf(10_000_00L, 2));

                ps.addBatch();
            }

            ps.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
