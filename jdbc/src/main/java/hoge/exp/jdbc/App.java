package hoge.exp.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        String databaseURL = "jdbc:postgresql:mydb";
        String user = "user01";
        String password = "password";

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
            ps.setInt(1, 0);
            ps.setString(2, "data1");
            ps.setString(3, "data2");

            ps.executeUpdate();

            conn.commit();
            ps.close();

            ps = conn.prepareStatement("SELECT id, data1, data2, created, updated FROM table01 WHERE id = ?");
            ps.setInt(1, 0);
            ResultSet rs = ps.executeQuery();
            rs.next();
            System.out.println(rs.getInt("id") + ", " +
                    rs.getString("data1") + ", " +
                    rs.getString("data2") + ", " +
                    rs.getTimestamp("created") + ", " +
                    rs.getTimestamp("updated"));

            conn.commit();
            rs.close();
            ps.close();

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
