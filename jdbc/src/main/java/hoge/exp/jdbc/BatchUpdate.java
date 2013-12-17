package hoge.exp.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchUpdate {
    public static void main(String[] args) {
        String databaseURL = "jdbc:postgresql:mydb";
        String user = "user01";
        String password = "password";
        int numOfRecordToInsert = 100;
        String data1Format = "data1-%07d";
        String data2Format = "data2-%07d";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(
                databaseURL, user, password)) {
            conn.setAutoCommit(false);

            conn.createStatement().executeUpdate("TRUNCATE TABLE table01");

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO table01 VALUES(?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            for (int i = 0; i < numOfRecordToInsert; i++) {
                ps.setInt(1, i);
                ps.setString(2, String.format(data1Format, i));
                ps.setString(3, String.format(data2Format, i));

                ps.addBatch();
            }

            ps.executeBatch();

            conn.commit();
            ps.close();

            ResultSet rs = conn.createStatement().executeQuery("SELECT id, data1, data2, created, updated FROM table01");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ", " +
                        rs.getString("data1") + ", " +
                        rs.getString("data2") + ", " +
                        rs.getTimestamp("created") + ", " +
                        rs.getTimestamp("updated"));
            }
            conn.commit();
            rs.close();

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM table01");
            rs.next();
            System.out.println(rs.getLong(1) + " records found.");
            conn.commit();
            rs.close();

            conn.createStatement().executeUpdate("TRUNCATE TABLE table01");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
