package ui;
import config.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class PendingAdminsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private SwingWorker<Void, Object[]> worker; // prevent multiple workers

    public PendingAdminsPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(10,45,55));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID","Username","Status"});

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(108,76,255));
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(47,52,70));

        JButton approveBtn = createStyledButton("Approve", new Color(108,76,255));
        JButton rejectBtn = createStyledButton("Reject", new Color(220,53,69));
        JButton refreshBtn = createStyledButton("Refresh", new Color(108,76,255));

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> approveAdmin());
        rejectBtn.addActionListener(e -> rejectAdmin());
        refreshBtn.addActionListener(e -> loadAdmins());

        loadAdmins(); // no need for invokeLater
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

    private void loadAdmins(){

        // stop previous worker if running
        if(worker != null && !worker.isDone()){
            worker.cancel(true);
        }

        model.setRowCount(0);

        worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {

                try {

                    Connection conn = DBConnection.getConnection();

                    String sql = "SELECT * FROM admins WHERE status='pending'";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();

                    while(rs.next()){

                        publish(new Object[]{
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("status")
                        });

                    }

                    conn.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {

                for(Object[] row : chunks){
                    model.addRow(row);
                }

            }
        };

        worker.execute();
    }

    private void approveAdmin(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Please select an admin first.");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE admins SET status='approved' WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1,id);
            pst.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(this,"Admin approved successfully.");

            loadAdmins();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void rejectAdmin(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Please select an admin first.");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reject this admin request?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm != JOptionPane.YES_OPTION) return;

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "DELETE FROM admins WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1,id);
            pst.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(this,"Admin request rejected.");

            loadAdmins();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}