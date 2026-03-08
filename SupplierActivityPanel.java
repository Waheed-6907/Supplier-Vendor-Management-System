package ui;
import config.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SupplierActivityPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public SupplierActivityPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(10,45,55)); // main background

        model = new DefaultTableModel();
        model.setColumnIdentifiers(
                new String[]{"ID","Name","Email","GST","CIN","Status"}
        );

        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(108,76,255)); // purple header
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(47,52,70)); // card color

        JButton approveBtn = createButton("Approve");
        JButton deleteBtn = createButton("Delete");
        JButton refreshBtn = createButton("Refresh");

        buttonPanel.add(approveBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSuppliers();

        approveBtn.addActionListener(e -> approveSupplier());
        deleteBtn.addActionListener(e -> deleteSupplier());
        refreshBtn.addActionListener(e -> loadSuppliers());
    }

    private JButton createButton(String text){

        JButton btn = new JButton(text);
        btn.setBackground(new Color(108,76,255)); // purple button
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));

        return btn;
    }

    private void loadSuppliers(){

        try{

            model.setRowCount(0);

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM suppliers WHERE status='pending'"
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

    private void approveSupplier(){

        int row = table.getSelectedRow();

        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a supplier first.");
            return;
        }

        int id = (int) model.getValueAt(row,0);
        String name = (String) model.getValueAt(row,1);
        String email = (String) model.getValueAt(row,2);

        try{

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE suppliers SET status='approved' WHERE id=?"
            );

            pst.setInt(1,id);
            pst.executeUpdate();

            conn.close();

            EmailSender.sendSupplierApprovalEmail(email,name);

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier approved successfully.\nApproval email sent."
            );

            loadSuppliers();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void deleteSupplier(){

        int row = table.getSelectedRow();

        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a supplier first.");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        try{

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM suppliers WHERE id=?"
            );

            pst.setInt(1,id);
            pst.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(this,"Supplier deleted.");

            loadSuppliers();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}