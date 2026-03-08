package ui;
import config.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RegisteredVendorsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public RegisteredVendorsPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(20,95,95)); // dashboard background

        // ===== CARD PANEL =====
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(37,40,70));
        card.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));

        // ===== TITLE =====
        JLabel title = new JLabel("Registered Vendors");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.WEST);

        // ===== TABLE MODEL =====
        model = new DefaultTableModel();
        model.setColumnIdentifiers(
                new String[]{"ID","Name","Email","Phone","GST","Status"}
        );

        table = new JTable(model);

        // ===== TABLE UI =====
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(95,75,180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(100,36));

        table.setGridColor(new Color(220,220,220));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        loadVendors();
    }

    private void loadVendors(){

        try{

            model.setRowCount(0);

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM vendors WHERE status='approved'"
            );

            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("gst_number"),
                        rs.getString("status")
                });

            }

            conn.close();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}