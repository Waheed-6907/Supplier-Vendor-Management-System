package ui;
import java.sql.*;
import config.DBConnection;

public class AdminDAO {

    public boolean login(String username, String password) {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM admins WHERE username=? AND password=? AND status='approved'";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            boolean success = rs.next();

            conn.close();

            return success;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void register(String username, String password) {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO admins (username,password,status) VALUES (?,?,?)";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, "pending");

            pst.executeUpdate();

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}