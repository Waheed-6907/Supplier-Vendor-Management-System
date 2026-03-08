package ui;

import config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminLoginFrame extends JFrame {

    public AdminLoginFrame() {

        setTitle("Admin Login");
        setSize(600, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel background = new JPanel();
        background.setBackground(new Color(10,45,55));
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400,380));
        card.setBackground(new Color(47,52,70));
        card.setLayout(null);

        JLabel title = new JLabel("Admin Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial",Font.BOLD,28));
        title.setBounds(110,30,250,40);

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(50,100,100,25);

        JTextField userField = new JTextField();
        userField.setBounds(50,130,300,35);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50,180,100,25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50,210,300,35);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(125,260,150,40);
        loginBtn.setBackground(new Color(108,76,255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());

        // ===== CREATE ACCOUNT LABEL =====
        JLabel createAccount = new JLabel("Create Account");
        createAccount.setForeground(new Color(180,180,255));
        createAccount.setFont(new Font("Segoe UI",Font.BOLD,13));
        createAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccount.setBounds(150,315,150,30);

        card.add(title);
        card.add(userLabel);
        card.add(userField);
        card.add(passLabel);
        card.add(passField);
        card.add(loginBtn);
        card.add(createAccount);

        background.add(card);
        add(background);

        setVisible(true);

        // ===== LOGIN ACTION =====
        loginBtn.addActionListener(e -> {

            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            try {

                Connection conn = DBConnection.getConnection();

                String sql = "SELECT * FROM admins WHERE username=? AND password=? AND status='approved'";
                PreparedStatement pst = conn.prepareStatement(sql);

                pst.setString(1,username);
                pst.setString(2,password);

                ResultSet rs = pst.executeQuery();

                if(rs.next()){

                    new AdminDashboard();
                    dispose();

                }else{

                    JOptionPane.showMessageDialog(this,"Invalid or Not Approved Yet!");

                }

                conn.close();

            }catch(Exception ex){
                ex.printStackTrace();
            }
        });

        // ===== OPEN REGISTRATION PAGE =====
        createAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                new AdminRegisterFrame();
                dispose();

            }
        });

    }
}