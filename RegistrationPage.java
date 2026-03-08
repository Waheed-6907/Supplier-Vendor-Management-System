package ui;

import dao.SupplierDAO;

import javax.swing.*;
import java.awt.*;

public class RegistrationPage extends JFrame {

    private JTextField txtName, txtEmail, txtPhone, txtAddress;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbStatus;

    // ===== THEME COLORS =====
    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;
    Color FIELD_COLOR = new Color(70,80,100);

    public RegistrationPage() {

        setTitle("Supplier Portal");
        setSize(600, 720);
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
        background.setBounds(0,0,600,720);
        setContentPane(background);

        // ===== CARD PANEL =====
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(100, 40, 400, 580);
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder());
        background.add(panel);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setBounds(80, 20, 250, 100);
        panel.add(title);

        int y = 80;

        panel.add(createLabel("Name", y));
        txtName = createField(y + 20);
        panel.add(txtName);

        y += 60;
        panel.add(createLabel("Email", y));
        txtEmail = createField(y + 20);
        panel.add(txtEmail);

        y += 60;
        panel.add(createLabel("Phone", y));
        txtPhone = createField(y + 20);
        panel.add(txtPhone);

        y += 60;
        panel.add(createLabel("Status", y));
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setBounds(50, y + 20, 300, 35);
        styleCombo(cmbStatus);
        panel.add(cmbStatus);

        y += 60;
        panel.add(createLabel("Password", y));
        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, y + 20, 300, 35);
        styleField(txtPassword);
        panel.add(txtPassword);

        y += 60;
        panel.add(createLabel("Address", y));
        txtAddress = createField(y + 20);
        panel.add(txtAddress);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(110, y + 80, 180, 40);
        styleButton(btnRegister);
        panel.add(btnRegister);

        JLabel backToLogin = new JLabel("Back to Login");
        backToLogin.setForeground(new Color(180, 200, 255));
        backToLogin.setBounds(150, y + 130, 150, 20);
        panel.add(backToLogin);

        // ================= REGISTER BUTTON =================
        btnRegister.addActionListener(e -> {

            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String status = cmbStatus.getSelectedItem().toString();
            String password = new String(txtPassword.getPassword()).trim();
            String address = txtAddress.getText().trim();

            if (!validateRegistrationInputs(name, email, password, phone, address)) {
                return;
            }

            SupplierDAO dao = new SupplierDAO();

            boolean result = dao.registerSupplier(
                    name,
                    email,
                    phone,
                    status,
                    password,
                    address
            );

            if (result) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed!");
            }
        });

        backToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new LoginPage();
                dispose();
            }
        });

        setVisible(true);
    }

    // ================= VALIDATION METHOD =================
    private boolean validateRegistrationInputs(String name,
                                               String email,
                                               String password,
                                               String phone,
                                               String address) {

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required fields.");
            return false;
        }

        if (!name.matches("^[a-zA-Z ]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Name can contain only letters and spaces.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Email Format!");
            return false;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters!");
            return false;
        }

        if (!phone.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this,
                    "Phone must be exactly 10 digits.");
            return false;
        }

        if (!address.matches("^[a-zA-Z0-9 ,]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Address contains invalid characters.");
            return false;
        }

        return true;
    }

    // ================= CLEAR FIELDS =================
    private void clearFields() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtPassword.setText("");
        txtAddress.setText("");
        cmbStatus.setSelectedIndex(0);
    }

    // ================= UI HELPERS =================
    private JLabel createLabel(String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("Segoe UI",Font.PLAIN,14));
        lbl.setBounds(50, y, 200, 20);
        return lbl;
    }

    private JTextField createField(int y) {
        JTextField txt = new JTextField();
        txt.setBounds(50, y, 300, 35);
        styleField(txt);
        return txt;
    }

    private void styleField(JTextField field){
        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setBackground(FIELD_COLOR);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
    }

    private void styleCombo(JComboBox<String> combo){
        combo.setFont(new Font("Segoe UI",Font.PLAIN,14));
        combo.setBackground(FIELD_COLOR);
        combo.setForeground(Color.WHITE);
    }

    private void styleButton(JButton button){
        button.setFont(new Font("Segoe UI",Font.BOLD,15));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}