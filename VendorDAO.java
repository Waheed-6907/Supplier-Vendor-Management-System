package dao;

import config.DBConnection;
import model.Vendor;

import java.sql.*;

public class VendorDAO {

    public Vendor login(String email, String password) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM vendor WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Vendor vendor = new Vendor();
                vendor.setId(rs.getInt("vendor_id"));
                vendor.setName(rs.getString("name"));
                vendor.setEmail(rs.getString("email"));
                return vendor;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}