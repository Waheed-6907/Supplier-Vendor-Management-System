package dao;

import config.DBConnection;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ===== ADD PRODUCT =====
    public boolean addProduct(String name, String categoryName, double price,
                              String description, String status,
                              int supplierId, int stock) {

        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product name cannot be empty");
            return false;
        }

        int categoryId = getCategoryId(categoryName);
        if (categoryId == -1) return false;

        String query = "INSERT INTO product (product_name, category_id, unit_price, description, status, supplier_id, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.setString(5, status);
            ps.setInt(6, supplierId);
            ps.setInt(7, stock);   // ✅ NEW

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE PRODUCT =====
    public boolean updateProduct(int productId, String name,
                                 String description, double price,
                                 String categoryName, String status,
                                 int stock) {

        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product name cannot be empty");
            return false;
        }

        int categoryId = getCategoryId(categoryName);
        if (categoryId == -1) return false;

        String query = "UPDATE product SET product_name=?, category_id=?, unit_price=?, description=?, status=?, stock=? WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.setString(5, status);
            ps.setInt(6, stock);        // ✅ NEW
            ps.setInt(7, productId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET PRODUCTS BY SUPPLIER =====
    public List<Object[]> getProductsBySupplier(int supplierId) {

        List<Object[]> list = new ArrayList<>();

        String query = "SELECT p.product_id, p.product_name, c.category_name, " +
                "p.unit_price, p.description, p.status, p.stock " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.category_id " +
                "WHERE p.supplier_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Object[] row = new Object[7];

                row[0] = rs.getInt("product_id");
                row[1] = rs.getString("product_name");
                row[2] = rs.getString("category_name") != null ? rs.getString("category_name") : "None";
                row[3] = rs.getDouble("unit_price");
                row[4] = rs.getString("description");
                row[5] = rs.getString("status");
                row[6] = rs.getInt("stock");   // ✅ NEW

                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== GET OR INSERT CATEGORY =====
    private int getCategoryId(String categoryName) {

        try (Connection con = DBConnection.getConnection()) {

            String check = "SELECT category_id FROM category WHERE category_name=?";
            PreparedStatement psCheck = con.prepareStatement(check);
            psCheck.setString(1, categoryName);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                return rs.getInt("category_id");
            } else {

                String insert = "INSERT INTO category (category_name) VALUES (?)";
                PreparedStatement psInsert = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                psInsert.setString(1, categoryName);
                psInsert.executeUpdate();

                ResultSet keys = psInsert.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}