package ui;

import config.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class ProfileFrame extends JFrame {

    public ProfileFrame(int vendorId) {
        System.out.println("ProfileFrame vendorId = " + vendorId);

        setTitle("Vendor Profile");
        setSize(900,650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ===== DARK TEAL GRADIENT BACKGROUND =====

        JPanel background = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(5,55,70),
                        getWidth(),getHeight(),
                        new Color(10,95,95)
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        background.setLayout(new GridBagLayout());

        // ===== CARD PANEL =====

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420,450));
        card.setBackground(new Color(44,52,70));
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40,40,40,40));

        try {

            Connection con = DBConnection.getConnection();

PreparedStatement ps = con.prepareStatement(
        "SELECT name,email,phone,address FROM vendor WHERE vendor_id=?"
);
ps.setInt(1,vendorId);

ResultSet rs = ps.executeQuery();
            if(rs.next()){

    JLabel title = new JLabel("Vendor Profile");
    title.setFont(new Font("Segoe UI",Font.BOLD,26));
    title.setForeground(Color.WHITE);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel name = new JLabel(rs.getString("name"));
    name.setFont(new Font("Segoe UI",Font.BOLD,20));
    name.setForeground(Color.WHITE);
    name.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel email = createInfoLabel("Email", rs.getString("email"));
JLabel phone = createInfoLabel("Phone", rs.getString("phone"));
JLabel gst = createInfoLabel("GST Number", rs.getString("gst_number"));
JLabel status = createInfoLabel("Status", rs.getString("status"));

    JButton backBtn = createThemeButton("Back to Dashboard");
    backBtn.addActionListener(e -> dispose());

    card.add(title);
    card.add(Box.createRigidArea(new Dimension(0,10)));
    card.add(name);
    card.add(Box.createRigidArea(new Dimension(0,30)));
    card.add(email);
    card.add(Box.createRigidArea(new Dimension(0,15)));
    card.add(phone);
card.add(Box.createRigidArea(new Dimension(0,15)));
card.add(gst);
card.add(Box.createRigidArea(new Dimension(0,15)));
card.add(status);
    card.add(Box.createVerticalGlue());
    card.add(backBtn);
}
else{

    JLabel msg = new JLabel("Vendor Profile Not Found");
    msg.setForeground(Color.WHITE);
    msg.setFont(new Font("Segoe UI",Font.BOLD,18));
    msg.setAlignmentX(Component.CENTER_ALIGNMENT);

    card.add(msg);
}
        }
        catch(Exception e){

    JLabel error = new JLabel("Database Error");
    error.setForeground(Color.WHITE);
    error.setFont(new Font("Segoe UI",Font.BOLD,18));
    error.setAlignmentX(Component.CENTER_ALIGNMENT);

    card.add(error);

    e.printStackTrace();
}

        background.add(card);
        add(background);

        setVisible(true);
    }

    // ===== INFO LABEL =====

    private JLabel createInfoLabel(String title,String value){

        JLabel label = new JLabel(
                "<html><div style='text-align:center;'>" +
                        "<span style='color:#C8D2E6; font-size:13px;'>"+title+"</span><br>" +
                        "<span style='color:white; font-size:15px;'>"+value+"</span>" +
                        "</div></html>"
        );

        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        return label;
    }

    // ===== BUTTON STYLE =====

    private JButton createThemeButton(String text){

        JButton btn = new JButton(text);

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250,40));
        btn.setPreferredSize(new Dimension(250,40));

        btn.setFont(new Font("Segoe UI",Font.BOLD,14));
        btn.setBackground(new Color(102,75,200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect

        btn.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseEntered(java.awt.event.MouseEvent evt){
                btn.setBackground(new Color(120,95,220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt){
                btn.setBackground(new Color(102,75,200));
            }

        });

        return btn;
    }
}