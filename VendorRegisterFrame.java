package ui;
import config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class VendorRegisterFrame extends JFrame {

    public VendorRegisterFrame() {

        setTitle("Vendor Registration");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background panel (dark theme)
        JPanel background = new JPanel();
        background.setBackground(new Color(12, 38, 48));
        background.setLayout(new GridBagLayout());

        // Card container
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 560));
        card.setBackground(new Color(45, 50, 70));
        card.setLayout(null);

        JLabel title = new JLabel("Vendor Registration");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(90, 20, 300, 40);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setBounds(50, 80, 100, 25);

        JTextField nameField = new JTextField();
        nameField.setBounds(50, 110, 300, 35);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setBounds(50, 160, 100, 25);

        JTextField emailField = new JTextField();
        emailField.setBounds(50, 190, 300, 35);

        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneLabel.setBounds(50, 240, 120, 25);

        JTextField phoneField = new JTextField();
        phoneField.setBounds(50, 270, 300, 35);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setBounds(50, 320, 100, 25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 350, 300, 35);

        JLabel gstLabel = new JLabel("GST Number");
        gstLabel.setForeground(Color.WHITE);
        gstLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gstLabel.setBounds(50, 400, 120, 25);

        JTextField gstField = new JTextField();
        gstField.setBounds(50, 430, 300, 35);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(125, 490, 150, 40);
        registerBtn.setBackground(new Color(110, 80, 210));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());

        card.add(title);
        card.add(nameLabel);
        card.add(nameField);
        card.add(emailLabel);
        card.add(emailField);
        card.add(phoneLabel);
        card.add(phoneField);
        card.add(passLabel);
        card.add(passField);
        card.add(gstLabel);
        card.add(gstField);
        card.add(registerBtn);

        background.add(card);
        add(background);

        setVisible(true);

        registerBtn.addActionListener(e -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passField.getPassword());
            String gst = gstField.getText().trim().toUpperCase();

            if (!name.matches("^[A-Za-z ]{3,50}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Name!");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Email!");
                return;
            }

            if (!phone.matches("^[0-9]{10}$")) {
                JOptionPane.showMessageDialog(this, "Phone must be exactly 10 digits!");
                return;
            }

            if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!]{6,}$")) {
                JOptionPane.showMessageDialog(this,
                        "Password must contain letters, numbers and be at least 6 characters!");
                return;
            }

            if (!gst.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]Z[0-9A-Z]$")) {
                JOptionPane.showMessageDialog(this, "Invalid GST Format!");
                return;
            }

            try {

                Connection conn = DBConnection.getConnection();

                String sql = "INSERT INTO vendors (name, email, phone, password, gst_number, status) VALUES (?, ?, ?, ?, ?, 'pending')";
                PreparedStatement pst = conn.prepareStatement(sql);

                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, phone);

                String hashedPassword = PasswordUtil.hashPassword(password);
                pst.setString(4, hashedPassword);

                pst.setString(5, gst);

                pst.executeUpdate();

                EmailSender.sendRegistrationEmail(email);

                JOptionPane.showMessageDialog(this,
                        "Vendor Registered Successfully!\nApproval email sent.");

                conn.close();

                new LoginSelectionFrame();
                dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this,
                        "Registration Failed!\n" + ex.getMessage());

                ex.printStackTrace();
            }
        });
    }
}