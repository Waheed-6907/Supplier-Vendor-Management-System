package ui;
import config.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RegisteredSuppliersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public RegisteredSuppliersPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(10,45,55)); // dark teal background

        model = new DefaultTableModel();
        model.setColumnIdentifiers(
                new String[]{"ID","Name","Email","GST","CIN","Status"}
        );

        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(220,220,220));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(100,35));
        table.getTableHeader().setBackground(new Color(108,76,255)); // purple header
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        loadSuppliers();
    }

    private void loadSuppliers(){

        try{

            model.setRowCount(0);

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM suppliers WHERE status='approved'"
            );

            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("gst_number"),
                        rs.getString("cin_number"),
                        rs.getString("status")
                });

            }

            conn.close();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}