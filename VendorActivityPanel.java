package ui;
import config.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VendorActivityPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public VendorActivityPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(
                new String[]{"ID","Name","Email","Phone","GST","Status"}
        );

        table = new JTable(model);

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(100,35));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton approveBtn = createStyledButton("Approve", new Color(46,204,113));
        JButton deleteBtn = createStyledButton("Delete", new Color(231,76,60));
        JButton refreshBtn = createStyledButton("Refresh", new Color(52,152,219));

        buttonPanel.add(approveBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadVendors();

        approveBtn.addActionListener(e -> approveVendor());
        deleteBtn.addActionListener(e -> deleteVendor());
        refreshBtn.addActionListener(e -> loadVendors());
    }

    private JButton createStyledButton(String text, Color color) {

        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void loadVendors(){

        try{

            model.setRowCount(0);

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM vendors WHERE status='pending'"
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

    private void approveVendor(){

        int row = table.getSelectedRow();

        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a vendor first.");
            return;
        }

        int id = (int) model.getValueAt(row,0);
        String name = (String) model.getValueAt(row,1);
        String email = (String) model.getValueAt(row,2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve this vendor?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm!=JOptionPane.YES_OPTION) return;

        try{

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE vendors SET status='approved' WHERE id=?"
            );

            pst.setInt(1,id);
            pst.executeUpdate();

            conn.close();

            // SEND EMAIL
            EmailSender.sendApprovalEmail(email,name);

            JOptionPane.showMessageDialog(
                    this,
                    "Vendor approved successfully.\nApproval email sent."
            );

            loadVendors();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void deleteVendor(){

        int row = table.getSelectedRow();

        if(row==-1){
            JOptionPane.showMessageDialog(this,"Please select a vendor first.");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this vendor?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm!=JOptionPane.YES_OPTION) return;

        try{

            Connection conn = DBConnection.getConnection();

            PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM vendors WHERE id=?"
            );

            pst.setInt(1,id);
            pst.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(this,"Vendor deleted successfully.");

            loadVendors();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
} 
