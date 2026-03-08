package ui;

import config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Paymentui extends JFrame {

    private int supplierId;
    private JTable table;
    private DefaultTableModel model;

    // ===== THEME COLORS (same as your project) =====
    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;

    public Paymentui(int supplierId) {

        this.supplierId = supplierId;

        setTitle("Supplier Payments");
        setSize(900,550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

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

        background.setLayout(new BorderLayout());
        setContentPane(background);

        // ===== TITLE =====
        JLabel title = new JLabel("Payments Received", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
        background.add(title,BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel();

        model.setColumnIdentifiers(new String[]{
                "Payment ID","Order ID","Amount","Payment Date","Payment Status"
        });

        table = new JTable(model);
        table.setRowHeight(25);
        table.setBackground(CARD_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(Color.GRAY);

        table.getTableHeader().setBackground(BUTTON_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        background.add(scrollPane,BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(0,0,0,0));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnBack = new JButton("Back to Dashboard");

        styleButton(btnRefresh);
        styleButton(btnBack);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnBack);

        background.add(bottomPanel,BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        loadPayments();

        btnRefresh.addActionListener(e -> loadPayments());

        btnBack.addActionListener(e -> {
            new SupplierDashboardFrame(supplierId);
            dispose();
        });

        setVisible(true);
    }

    private void loadPayments(){

        model.setRowCount(0);

        try(Connection con = DBConnection.getConnection()){

            // ONLY READ PAYMENT TABLE
            String query =
                    "SELECT payment_id, order_id, amount, payment_date, payment_status FROM payment";

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("order_id"),
                        rs.getDouble("amount"),
                        rs.getDate("payment_date"),
                        rs.getString("payment_status")
                });

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void styleButton(JButton btn){

        btn.setFont(new Font("Segoe UI",Font.BOLD,14));
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

    }
}