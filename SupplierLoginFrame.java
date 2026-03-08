package ui;
import config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SupplierLoginFrame extends JFrame {

    public SupplierLoginFrame() {

        setTitle("Supplier Login");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel background = new JPanel();
        background.setBackground(new Color(20, 30, 90));
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 350));
        card.setBackground(new Color(40, 120, 200));
        card.setLayout(null);

        JLabel title = new JLabel("Supplier Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(110, 30, 250, 40);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(50, 100, 100, 25);

        JTextField emailField = new JTextField();
           emailField.setBounds(50, 130, 300, 35);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 180, 100, 25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 210, 300, 35);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(125, 270, 150, 40);
        loginBtn.setBackground(new Color(120, 200, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        card.add(title);
        card.add(emailLabel);
        card.add(emailField);
        card.add(passLabel);
        card.add(passField);
        card.add(loginBtn);

        background.add(card);
        add(background);

        setVisible(true);

        loginBtn.addActionListener(e -> {

            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());

            try {
                Connection conn = DBConnection.getConnection();

                String sql = "SELECT * FROM suppliers WHERE email=? AND password=? AND status='approved'";
                PreparedStatement pst = conn.prepareStatement(sql);

                pst.setString(1, email);
                String hashedPassword = PasswordUtil.hashPassword(password);
                pst.setString(2, hashedPassword);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    
                    // You can open Supplier Dashboard here
                    // new SupplierDashboard();
                    // dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or not approved yet!");
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
