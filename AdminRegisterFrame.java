package ui;
import config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminRegisterFrame extends JFrame {

    public AdminRegisterFrame() {

        setTitle("Admin Registration");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel background = new JPanel();
        background.setBackground(new Color(10,45,55)); 
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 350));
        card.setBackground(new Color(47,52,70));
        card.setLayout(null);

        JLabel title = new JLabel("Admin Registration");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(90, 30, 300, 40);

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(50, 100, 100, 25);

        JTextField userField = new JTextField();
        userField.setBounds(50, 130, 300, 35);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 180, 100, 25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 210, 300, 35);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(125, 270, 150, 40);
        registerBtn.setBackground(new Color(108,76,255)); // updated button
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());

        card.add(title);
        card.add(userLabel);
        card.add(userField);
        card.add(passLabel);
        card.add(passField);
        card.add(registerBtn);

        background.add(card);
        add(background);

        setVisible(true);

        registerBtn.addActionListener(e -> registerAdmin(userField, passField));
    }

    private void registerAdmin(JTextField userField, JPasswordField passField) {

        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please fill all fields");
            return;
        }

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO admins (username, password) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            int result = pst.executeUpdate();

            if(result > 0){
                JOptionPane.showMessageDialog(this,"Admin registered successfully!");
            }

            conn.close();

            new LoginSelectionFrame();
            dispose();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Registration Error:\n" + ex.getMessage());
        }
    }
}