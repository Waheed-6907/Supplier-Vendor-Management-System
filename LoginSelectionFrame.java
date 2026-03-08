package ui;
import javax.swing.*;
import java.awt.*;

public class LoginSelectionFrame extends JFrame {

    public LoginSelectionFrame() {

        setTitle("Login As");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel background = new JPanel();
        background.setBackground(new Color(10, 45, 55)); // dark teal background
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 450));
        card.setBackground(new Color(47, 52, 70)); // dark slate card
        card.setLayout(null);

        JLabel title = new JLabel("Login As");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(140, 40, 200, 40);

        JButton vendorBtn = createButton("Vendor");
        vendorBtn.setBounds(75, 120, 250, 60);

        JButton supplierBtn = createButton("Supplier");
        supplierBtn.setBounds(75, 200, 250, 60);

        JButton adminBtn = createButton("Admin");
        adminBtn.setBounds(75, 280, 250, 60);

        JLabel footerText = new JLabel("Don't have an account?");
        footerText.setForeground(new Color(200, 200, 200));
        footerText.setBounds(90, 360, 180, 25);

        JLabel signupLabel = new JLabel("<HTML><U>Sign up now</U></HTML>");
        signupLabel.setForeground(new Color(108, 76, 255)); // purple link
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.setBounds(260, 360, 120, 25);

        card.add(title);
        card.add(vendorBtn);
        card.add(supplierBtn);
        card.add(adminBtn);
        card.add(footerText);
        card.add(signupLabel);

        background.add(card);
        add(background);

        setVisible(true);

        // ===== BUTTON ACTIONS =====

        adminBtn.addActionListener(e -> {
            new AdminLoginFrame();
            dispose();
        });

        vendorBtn.addActionListener(e -> {
            new VendorRegisterFrame();
            dispose();
        });

        supplierBtn.addActionListener(e -> {
            new SupplierRegisterFrame();
            dispose();
        });

        signupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new AdminRegisterFrame();
                dispose();
            }
        });
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(108, 76, 255)); // purple buttons
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());

        return button;
    }
}