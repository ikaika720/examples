package hoge.exp.jpa;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataLoader {

    public static void main(String[] args) {
        String databaseURL = "jdbc:postgresql:mydb";
        String user = "user01";
        String password = "password";

        int numOfRecordToInsert = 10_000;

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
