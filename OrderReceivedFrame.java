package ui;

import config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderReceivedFrame extends JFrame {

    private int supplierId;
    private JTable table;
    private DefaultTableModel model;

    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;

    public OrderReceivedFrame(int supplierId) {

        this.supplierId = supplierId;

        setTitle("Order Received");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        JPanel background = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,BACKGROUND_START,
                        getWidth(),getHeight(),BACKGROUND_END
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        background.setLayout(new BorderLayout(15,15));
        setContentPane(background);

        JLabel title = new JLabel("Orders Received", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_COLOR);
        background.add(title, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Order ID", "Product ID", "Units",
                "Delivery Date", "Status", "Vendor ID"
        });

        table = new JTable(model);
        table.setRowHeight(28);
        table.setBackground(CARD_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(BUTTON_COLOR);
        table.setSelectionBackground(BUTTON_COLOR);
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setBackground(BUTTON_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(BUTTON_COLOR));

        background.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton btnApprove = createButton("Approve");
        JButton btnReject = createButton("Reject");
        JButton btnProcessing = createButton("Processing");
        JButton btnCompleted = createButton("Completed");
        JButton btnBack = createButton("Back to Dashboard");

        panel.add(btnApprove);
        panel.add(btnReject);
        panel.add(btnProcessing);
        panel.add(btnCompleted);
        panel.add(btnBack);

        background.add(panel, BorderLayout.SOUTH);

        loadOrders();

        // APPROVE
        btnApprove.addActionListener(e -> {

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select an order first!");
                return;
            }

            int orderId = Integer.parseInt(model.getValueAt(selectedRow,0).toString());
            String currentStatus = model.getValueAt(selectedRow,4).toString();

            if (!currentStatus.equalsIgnoreCase("PENDING")) {
                JOptionPane.showMessageDialog(this,
                        "Only PENDING orders can be approved!");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {

                String orderQuery =
                        "SELECT product_id, units FROM v_orders WHERE order_id=?";
                PreparedStatement psOrder =
                        con.prepareStatement(orderQuery);
                psOrder.setInt(1, orderId);
                ResultSet rsOrder = psOrder.executeQuery();

                if (rsOrder.next()) {

                    int productId = rsOrder.getInt("product_id");
                    int units = rsOrder.getInt("units");

                    String stockQuery =
                            "SELECT stock FROM product WHERE product_id=?";
                    PreparedStatement psStock =
                            con.prepareStatement(stockQuery);
                    psStock.setInt(1, productId);
                    ResultSet rsStock = psStock.executeQuery();

                    if (rsStock.next()) {

                        int availableStock = rsStock.getInt("stock");

                        if (units > availableStock) {

                            String rejectQuery =
                                    "UPDATE v_orders SET status='REJECTED' WHERE order_id=?";
                            PreparedStatement psReject =
                                    con.prepareStatement(rejectQuery);
                            psReject.setInt(1, orderId);
                            psReject.executeUpdate();

                            JOptionPane.showMessageDialog(this,
                                    "Out of stock!\nPlease place order again with smaller quantity.");

                        } else {

                            String approveQuery =
                                    "UPDATE v_orders SET status='APPROVED' WHERE order_id=?";
                            PreparedStatement psApprove =
                                    con.prepareStatement(approveQuery);
                            psApprove.setInt(1, orderId);
                            psApprove.executeUpdate();

                            String updateStockQuery =
                                    "UPDATE product SET stock = stock - ? WHERE product_id=?";
                            PreparedStatement psUpdate =
                                    con.prepareStatement(updateStockQuery);
                            psUpdate.setInt(1, units);
                            psUpdate.setInt(2, productId);
                            psUpdate.executeUpdate();

                            JOptionPane.showMessageDialog(this,
                                    "Order Approved Successfully!");
                        }
                    }
                }

                loadOrders();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnReject.addActionListener(e -> updateStatus("REJECTED"));
        btnProcessing.addActionListener(e -> updateStatus("PROCESSING"));
        btnCompleted.addActionListener(e -> updateStatus("COMPLETED"));

        btnBack.addActionListener(e -> {
            new SupplierDashboardFrame(supplierId);
            dispose();
        });

        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateStatus(String status) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first!");
            return;
        }

        int orderId = Integer.parseInt(model.getValueAt(selectedRow,0).toString());
        String currentStatus = model.getValueAt(selectedRow,4).toString();

        if(currentStatus.equalsIgnoreCase("REJECTED") ||
           currentStatus.equalsIgnoreCase("COMPLETED")){

            JOptionPane.showMessageDialog(this,
                    "Cannot update status of REJECTED or COMPLETED orders!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String query =
                    "UPDATE v_orders SET status=? WHERE order_id=?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, orderId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Order status updated to " + status);

            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadOrders() {

        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection()) {

            String query =
                    "SELECT v.order_id, v.product_id, v.units, v.delivery_date, v.status, v.vendor_id " +
                    "FROM v_orders v " +
                    "JOIN product p ON v.product_id = p.product_id " +
                    "WHERE p.supplier_id=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, supplierId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("units"),
                        rs.getDate("delivery_date"),
                        rs.getString("status"),
                        rs.getInt("vendor_id")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}