package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

public class SupplierDAO {

    // PASSWORD HASH METHOD 
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // REGISTER METHOD
    public boolean registerSupplier(String name, String email, String phone,
                                    String status, String password, String address) {

        String query = "INSERT INTO supplier (supplier_name, email, phone, status, password, address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, status);
            ps.setString(5, hashPassword(password)); // store hashed password
            ps.setString(6, address);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // LOGIN METHOD 
    public int login(String email, String password) {

        String query = "SELECT supplier_id FROM supplier WHERE email=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, hashPassword(password)); // compare hashed password

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("supplier_id"); // return ID if login success
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // login failed
    }
}