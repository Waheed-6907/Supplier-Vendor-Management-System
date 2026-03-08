package ui;

import dao.SupplierDAO;
import config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPanel panel;

    // ===== THEME COLORS =====
    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;
    Color FIELD_COLOR = new Color(70,80,100);

    public LoginPage() {

        setTitle("Supplier Portal");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // ===== GRADIENT BACKGROUND =====
        JPanel background = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,BACKGROUND_START,
                        getWidth(),getHeight(),BACKGROUND_END
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        background.setLayout(null);
        background.setBounds(0,0,600,500);
        setContentPane(background);

        // ===== LOGIN PANEL =====
        panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(400,380); // slightly increased for new elements
        panel.setBackground(CARD_COLOR);
        background.add(panel);

        centerPanel();

        // ===== TITLE =====
        JLabel title = new JLabel("Supplier Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setBounds(90, 30, 250, 40);
        panel.add(title);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setForeground(TEXT_COLOR);
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setBounds(50, 100, 100, 20);
        panel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(50, 125, 300, 35);
        styleField(txtEmail);
        panel.add(txtEmail);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(TEXT_COLOR);
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setBounds(50, 170, 100, 20);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 195, 300, 35);
        styleField(txtPassword);
        panel.add(txtPassword);

        // ===== SHOW PASSWORD CHECKBOX =====
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setBounds(50, 235, 150, 20);
        showPass.setBackground(CARD_COLOR);
        showPass.setForeground(new Color(180, 200, 255));
        showPass.setFocusPainted(false);
        showPass.addActionListener(e -> {
            if(showPass.isSelected()) {
                txtPassword.setEchoChar((char)0);
            } else {
                txtPassword.setEchoChar('*');
            }
        });
        panel.add(showPass);

        // ===== LOGIN BUTTON =====
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(110, 265, 180, 40);
        styleButton(btnLogin);
        panel.add(btnLogin);

        // ===== CREATE ACCOUNT LABEL =====
        JLabel createAccount = new JLabel("Create Account");
        createAccount.setForeground(new Color(180, 200, 255));
        createAccount.setBounds(140, 315, 150, 20);
        createAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(createAccount);

        // ===== FORGOT PASSWORD LABEL =====
        JLabel forgotPassword = new JLabel("Forgot Password?");
        forgotPassword.setForeground(new Color(180,200,255));
        forgotPassword.setBounds(140, 345, 150, 20);
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(forgotPassword);

        // ===== LOGIN ACTION =====
        btnLogin.addActionListener(e -> {

            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());

            SupplierDAO dao = new SupplierDAO();
            int supplierId = dao.login(email, password);

            if (supplierId != -1) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new SupplierDashboardFrame(supplierId);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password!");
            }
        });

        // ===== CREATE ACCOUNT ACTION =====
        createAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new SupplierRegisterFrame();
            }
        });

        // ===== FORGOT PASSWORD ACTION =====
        forgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                String email = JOptionPane.showInputDialog(
                        LoginPage.this,
                        "Enter your registered email:"
                );

                if(email == null || email.isEmpty()) return;

                try(Connection con = DBConnection.getConnection()){

                    String checkQuery = "SELECT supplier_id FROM supplier WHERE email=?";
                    PreparedStatement ps = con.prepareStatement(checkQuery);
                    ps.setString(1,email);

                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){

                        String newPassword = JOptionPane.showInputDialog(
                                LoginPage.this,
                                "Enter new password:"
                        );

                        if(newPassword == null || newPassword.isEmpty()) return;

                        String hashed = hashPassword(newPassword);

                        String updateQuery =
                                "UPDATE supplier SET password=? WHERE email=?";

                        PreparedStatement ps2 =
                                con.prepareStatement(updateQuery);

                        ps2.setString(1,hashed);
                        ps2.setString(2,email);

                        ps2.executeUpdate();

                        JOptionPane.showMessageDialog(
                                LoginPage.this,
                                "Password Reset Successful!"
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                                LoginPage.this,
                                "Email not found!"
                        );
                    }

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        // ===== WINDOW RESIZE LISTENER =====
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                centerPanel();
            }
        });

        setVisible(true);
    }

    // ===== CENTER PANEL =====
    private void centerPanel() {

        int x = (getWidth() - panel.getWidth()) / 2;
        int y = (getHeight() - panel.getHeight()) / 2;

        panel.setLocation(x, y);
    }

    // ===== HASH PASSWORD =====
    private String hashPassword(String password){

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for(byte b : hash){
                String s = Integer.toHexString(0xff & b);
                if(s.length()==1) hex.append('0');
                hex.append(s);
            }
            return hex.toString();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // ===== FIELD STYLE =====
    private void styleField(JTextField field){
        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setBackground(FIELD_COLOR);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
    }

    // ===== BUTTON STYLE =====
    private void styleButton(JButton button){
        button.setFont(new Font("Segoe UI",Font.BOLD,15));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}