package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VendorDashboard extends JFrame {

    public VendorDashboard(int vendorId) {

        setTitle("Vendor Portal");
        setSize(900,650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
        card.setPreferredSize(new Dimension(420,420));
        card.setBackground(new Color(44,52,70));
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40,40,40,40));

        // ===== HEADER =====

        JLabel title = new JLabel("Vendor Portal");
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel vendorInfo = new JLabel("Vendor ID: " + vendorId);
        vendorInfo.setFont(new Font("Segoe UI",Font.PLAIN,14));
        vendorInfo.setForeground(new Color(200,210,230));
        vendorInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== BUTTONS =====

        
        JButton buyBtn = createThemeButton("Buy Product");
        JButton ordersBtn = createThemeButton("Manage Orders");
        JButton logoutBtn = createThemeButton("Logout");

 logoutBtn.setBackground(new Color(220, 53, 69));   // red
logoutBtn.setForeground(Color.WHITE);
logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {

    public void mouseEntered(java.awt.event.MouseEvent evt) {
        logoutBtn.setBackground(new Color(200, 35, 51));
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
        logoutBtn.setBackground(new Color(220, 53, 69));
    }
});
    

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0,10)));
        card.add(vendorInfo);
        card.add(Box.createRigidArea(new Dimension(0,50)));

        
        card.add(buyBtn);
        card.add(Box.createRigidArea(new Dimension(0,15)));

        card.add(ordersBtn);
        card.add(Box.createVerticalGlue());

        card.add(logoutBtn);

        background.add(card);
        add(background);

     

        
        buyBtn.addActionListener(e -> {
            new BuyProductFrame(vendorId);
            dispose();
        });

        ordersBtn.addActionListener(e -> {
            new MyOrdersFrame(vendorId);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
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