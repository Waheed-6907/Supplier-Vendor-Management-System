package dao;

import config.DBConnection;
import java.sql.*;

public class OrderDAO2 {

   public int getTotalOrders(int supplierId) {

    String query = """
            SELECT COUNT(DISTINCT o.order_id)
            FROM orders o
            JOIN order_details od ON o.order_id = od.order_id
            JOIN product p ON od.product_id = p.product_id
            WHERE p.supplier_id = ?
            """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {

        ps.setInt(1, supplierId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}

public int getPendingOrders(int supplierId) {

    String query = """
            SELECT COUNT(DISTINCT o.order_id)
            FROM orders o
            JOIN order_details od ON o.order_id = od.order_id
            JOIN product p ON od.product_id = p.product_id
            WHERE p.supplier_id = ?
            AND o.order_status = 'Pending'
            """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {

        ps.setInt(1, supplierId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}
}