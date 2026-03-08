package ui;

import config.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {

    private JTextField txtName, txtEmail, txtPhone, txtAddress;
    private JPasswordField txtPassword;
    private JCheckBox showPasswordCheck;

    public RegisterFrame() {

        setTitle("Vendor Registration");
        setSize(900, 780);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel background = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(25, 70, 130),
                        0, getHeight(), new Color(40, 120, 180)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(550, 680));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 230, 255), 2),
                new EmptyBorder(50, 70, 50, 70)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        JLabel title = new JLabel("Vendor Registration", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 40, 0);
        card.add(title, gbc);

        // NAME
        addLabel(card, gbc, row++, "Name");
        txtName = createField();
        ((AbstractDocument) txtName.getDocument())
                .setDocumentFilter(new LetterOnlyFilter());
        addField(card, gbc, row++, txtName);

        // EMAIL
        addLabel(card, gbc, row++, "Email");
        txtEmail = createField();
        addField(card, gbc, row++, txtEmail);

        // PASSWORD
        addLabel(card, gbc, row++, "Password");
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        addField(card, gbc, row++, txtPassword);

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setForeground(Color.WHITE);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordCheck.addActionListener(e -> togglePassword());

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(showPasswordCheck, gbc);

        // PHONE
        addLabel(card, gbc, row++, "Phone");
        txtPhone = createField();
        ((AbstractDocument) txtPhone.getDocument())
                .setDocumentFilter(new NumberOnlyFilter(10));
        addField(card, gbc, row++, txtPhone);

        // ADDRESS
        addLabel(card, gbc, row++, "Address");
        txtAddress = createField();
        ((AbstractDocument) txtAddress.getDocument())
                .setDocumentFilter(new AddressFilter());
        addField(card, gbc, row++, txtAddress);

        JButton btnRegister = createThemeButton("Register");

        gbc.gridy = row++;
        gbc.insets = new Insets(30, 0, 20, 0);
        card.add(btnRegister, gbc);

        background.add(card);
        add(background);

        btnRegister.addActionListener(e -> registerVendor());

        setVisible(true);
    }

    // ================= VALIDATION =================

    private boolean validateInputs(String name, String email,
                                   String password, String phone,
                                   String address) {

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields.");
            return false;
        }

        if (!Pattern.matches("^[a-zA-Z ]+$", name)) {
            showError("Name can contain only letters and spaces.");
            return false;
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            showError("Invalid Email Format!");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters!");
            return false;
        }

        if (!phone.matches("^[0-9]{10}$")) {
            showError("Phone must be exactly 10 digits.");
            return false;
        }

        if (!address.matches("^[a-zA-Z0-9 ,]+$")) {
            showError("Address contains invalid characters.");
            return false;
        }

        return true;
    }

    private void registerVendor() {

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (!validateInputs(name, email, password, phone, address))
            return;

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement checkPs =
                    con.prepareStatement("SELECT * FROM vendor WHERE email=?");
            checkPs.setString(1, email);

            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                showError("Email already registered!");
                return;
            }

            String hashedPassword =
                    BCrypt.hashpw(password, BCrypt.gensalt());

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO vendor (name, email, password, phone, address) VALUES (?,?,?,?,?)"
            );

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.setString(4, phone);
            ps.setString(5, address);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration Successful!");
            new LoginFrame();
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
        }
    }

    // ================= FILTERS =================

    class LetterOnlyFilter extends DocumentFilter {

        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {
            if (string.matches("[a-zA-Z ]+"))
                super.insertString(fb, offset, string, attr);
            else
                showError("Name can contain only letters and spaces.");
        }

        public void replace(FilterBypass fb, int offset, int length,
                            String text, AttributeSet attrs)
                throws BadLocationException {

            if (text.matches("[a-zA-Z ]*"))
                super.replace(fb, offset, length, text, attrs);
            else
                showError("Name can contain only letters and spaces.");
        }
    }

    class NumberOnlyFilter extends DocumentFilter {

        private int maxLength;

        public NumberOnlyFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {

            if (!string.matches("[0-9]+")) {
                showError("Phone must contain digits only.");
                return;
            }

            if (fb.getDocument().getLength() + string.length() > maxLength) {
                showError("Phone number cannot exceed " + maxLength + " digits.");
                return;
            }

            super.insertString(fb, offset, string, attr);
        }

        public void replace(FilterBypass fb, int offset, int length,
                            String text, AttributeSet attrs)
                throws BadLocationException {

            if (!text.matches("[0-9]*")) {
                showError("Phone must contain digits only.");
                return;
            }

            super.replace(fb, offset, length, text, attrs);
        }
    }

    class AddressFilter extends DocumentFilter {

        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {

            if (string.matches("[a-zA-Z0-9 ,]+"))
                super.insertString(fb, offset, string, attr);
            else
                showError("Address can contain letters, numbers, space and comma only.");
        }

        public void replace(FilterBypass fb, int offset, int length,
                            String text, AttributeSet attrs)
                throws BadLocationException {

            if (text.matches("[a-zA-Z0-9 ,]*"))
                super.replace(fb, offset, length, text, attrs);
            else
                showError("Address can contain letters, numbers, space and comma only.");
        }
    }

    // ================= HELPERS =================

    private void togglePassword() {
        txtPassword.setEchoChar(
                showPasswordCheck.isSelected() ? (char) 0 : '*'
        );
    }

    private void showError(String message) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this, message);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc,
                          int row, String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          int row, JComponent comp) {
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(comp, gbc);
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    private JButton createThemeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(70, 170, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setPreferredSize(new Dimension(0, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}