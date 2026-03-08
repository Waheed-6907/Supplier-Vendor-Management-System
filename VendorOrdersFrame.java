package ui;

import config.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VendorOrdersFrame extends JFrame {

    private int vendorId;

    public VendorOrdersFrame(int vendorId) {

        this.vendorId = vendorId;

        setTitle("My Orders (Status View)");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ===== DARK TEAL BACKGROUND =====
        JPanel background = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(5,55,70),
                        0,getHeight(),new Color(10,95,95)
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        background.setLayout(new BorderLayout());
        background.setBorder(new EmptyBorder(20,20,20,20));

        JLabel titleLabel = new JLabel("My Orders",SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI",Font.BOLD,26));
        titleLabel.setBorder(new EmptyBorder(10,0,20,0));

        // ===== TABS =====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI",Font.BOLD,14));

        tabbedPane.addTab("Pending",createOrderPanel("PENDING"));
        tabbedPane.addTab("Approved",createOrderPanel("APPROVED"));
        tabbedPane.addTab("Rejected",createOrderPanel("REJECTED"));
        tabbedPane.addTab("Completed",createOrderPanel("COMPLETED"));

        background.add(titleLabel,BorderLayout.NORTH);
        background.add(tabbedPane,BorderLayout.CENTER);

        add(background);
        setVisible(true);
    }

    private JScrollPane createOrderPanel(String status){

        String[] columns = {
                "Order ID",
                "Category",
                "Product Name",
                "Units",
                "Delivery Date",
                "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns,0);
        JTable table = new JTable(model);

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(220,220,220));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(102,75,200));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI",Font.BOLD,14));

        loadOrders(model,status);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private void loadOrders(DefaultTableModel model,String status){

        try(Connection con = DBConnection.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "SELECT o.order_id, c.category_name, p.product_name, " +
                    "o.units, o.delivery_date, o.status " +
                    "FROM v_orders o " +
                    "JOIN product p ON o.product_id = p.product_id " +
                    "JOIN category c ON p.category_id = c.category_id " +
                    "WHERE o.vendor_id = ? AND o.status = ? AND o.is_deleted = 0"
            );

            ps.setInt(1,vendorId);
            ps.setString(2,status);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("category_name"),
                        rs.getString("product_name"),
                        rs.getInt("units"),
                        rs.getDate("delivery_date"),
                        rs.getString("status")
                });
            }

        }catch(Exception e){

            e.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Error loading orders: "+e.getMessage());
        }
    }
}