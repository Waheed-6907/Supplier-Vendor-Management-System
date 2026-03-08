package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import config.DBConnection;

public class SupplierDashboardFrame extends JFrame {

    private int supplierId;

    // ===== THEME COLORS =====
    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;
    Color FIELD_COLOR = new Color(70,80,100);

    public SupplierDashboardFrame(int supplierId) {

        this.supplierId = supplierId;

        setTitle("Supplier Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // ===== GRADIENT BACKGROUND =====
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
        background.setLayout(null);
        setContentPane(background);

        // ========== SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBounds(0, 0, 250, 700);
        sidebar.setBackground(CARD_COLOR);
        background.add(sidebar);

        JLabel logo = new JLabel("SUPPLIER PANEL");
        logo.setForeground(TEXT_COLOR);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setBounds(40, 30, 200, 30);
        sidebar.add(logo);

        JButton btnDashboard = createSidebarButton("Dashboard", 100);
        JButton btnProducts = createSidebarButton("Products", 160);
        JButton btnOrders = createSidebarButton("Orders", 220);
        JButton btnPayments = createSidebarButton("Payments", 280);
        JButton btnLogout = createSidebarButton("Logout", 340);

        sidebar.add(btnDashboard);
        sidebar.add(btnProducts);
        sidebar.add(btnOrders);
        sidebar.add(btnPayments);
        sidebar.add(btnLogout);

        // ========== HEADER ==========
        JPanel header = new JPanel();
        header.setLayout(null);
        header.setBounds(250, 0, 950, 80);
        header.setBackground(CARD_COLOR);
        background.add(header);

        JLabel title = new JLabel("Dashboard Overview");
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(30, 20, 300, 40);
        header.add(title);

        // ========== MAIN PANEL ==========
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(250, 80, 950, 620);
        mainPanel.setOpaque(false);
        background.add(mainPanel);

        JPanel card1 = createCard("Total Products",
                String.valueOf(getTotalProducts()), 50, 50);

        JPanel card2 = createCard("Active Products",
                String.valueOf(getActiveProducts()), 350, 50);

        JPanel card3 = createCard("Total Orders",
                String.valueOf(getTotalOrders()), 650, 50);

        JPanel card4 = createCard("Pending Orders",
                String.valueOf(getPendingOrders()), 50, 250);

        JPanel card5 = createCard("Total Earnings",
                "Rs." + getTotalEarnings(), 350, 250);

        JPanel card6 = createCard("Completed Payments",
                String.valueOf(getCompletedPayments()), 650, 250);

        mainPanel.add(card1);
        mainPanel.add(card2);
        mainPanel.add(card3);
        mainPanel.add(card4);
        mainPanel.add(card5);
        mainPanel.add(card6);

        // ========== BUTTON ACTIONS ==========

        btnProducts.addActionListener(e -> {
            new ProductManagementFrame(supplierId);
            dispose();
        });

        btnOrders.addActionListener(e -> {
            new OrderReceivedFrame(supplierId);
            dispose();
        });

        btnPayments.addActionListener(e -> {
            new Paymentui(supplierId);
            dispose();
        });

        btnLogout.addActionListener(e -> {
            new LoginPage();
            dispose();
        });

        setVisible(true);

        // LOW STOCK ALERT
        checkLowStock();
    }

    private JButton createSidebarButton(String text, int y) {
        JButton button = new JButton(text);
        button.setBounds(20, y, 210, 40);
        button.setFocusPainted(false);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createCard(String title, String value, int x, int y) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(x, y, 250, 120);
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(BUTTON_COLOR, 2));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setBounds(20, 20, 200, 20);

        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(TEXT_COLOR);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setBounds(20, 50, 200, 40);

        panel.add(lblTitle);
        panel.add(lblValue);

        return panel;
    }

    // ===== LOW STOCK ALERT =====

    private void checkLowStock() {

        String message = "";

        String query =
                "SELECT product_name, stock FROM product " +
                "WHERE supplier_id=? AND stock<=5";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){

            ps.setInt(1, supplierId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                message += rs.getString("product_name")
                        + " (Stock: "
                        + rs.getInt("stock")
                        + ")\n";

            }

            if(!message.isEmpty()){

                JOptionPane.showMessageDialog(
                        this,
                        "Low Stock Alert!\n\n" + message,
                        "Stock Warning",
                        JOptionPane.WARNING_MESSAGE
                );

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // ===== PRODUCT COUNTS =====

    private int getTotalProducts() {
        String query = "SELECT COUNT(*) FROM product WHERE supplier_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getActiveProducts() {
        String query = "SELECT COUNT(*) FROM product WHERE supplier_id=? AND status='Active'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getTotalOrders() {
        String query =
                "SELECT COUNT(*) FROM v_orders o " +
                "JOIN product p ON o.product_id = p.product_id " +
                "WHERE p.supplier_id=? AND o.is_deleted=0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getPendingOrders() {
        String query =
                "SELECT COUNT(*) FROM v_orders o " +
                "JOIN product p ON o.product_id = p.product_id " +
                "WHERE p.supplier_id=? AND o.status='PENDING' AND o.is_deleted=0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private double getTotalEarnings() {

        double total = 0;

        String query =
                "SELECT SUM(p.amount) " +
                "FROM payment p " +
                "JOIN v_orders o ON p.order_id = o.order_id " +
                "JOIN product pr ON o.product_id = pr.product_id " +
                "WHERE pr.supplier_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    private int getCompletedPayments() {

        int count = 0;

        String query =
                "SELECT COUNT(*) " +
                "FROM payment p " +
                "JOIN v_orders o ON p.order_id = o.order_id " +
                "JOIN product pr ON o.product_id = pr.product_id " +
                "WHERE pr.supplier_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}