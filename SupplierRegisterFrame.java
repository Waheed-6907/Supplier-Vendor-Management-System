package ui;
import config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SupplierRegisterFrame extends JFrame {

    public SupplierRegisterFrame() {

        setTitle("Supplier Registration");
        setSize(600,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel background = new JPanel();
        background.setBackground(new Color(12,38,48));
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400,600));
        card.setBackground(new Color(45,50,70));
        card.setLayout(null);

        JLabel title = new JLabel("Supplier Registration");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,24));
        title.setBounds(80,20,300,40);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        nameLabel.setBounds(50,80,100,25);

        JTextField nameField = new JTextField();
        nameField.setBounds(50,110,300,35);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        emailLabel.setBounds(50,160,100,25);

        JTextField emailField = new JTextField();
        emailField.setBounds(50,190,300,35);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        passLabel.setBounds(50,240,100,25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50,270,300,35);

        JLabel gstLabel = new JLabel("GST Number");
        gstLabel.setForeground(Color.WHITE);
        gstLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        gstLabel.setBounds(50,320,120,25);

        JTextField gstField = new JTextField();
        gstField.setBounds(50,350,300,35);

        JLabel cinLabel = new JLabel("CIN Number");
        cinLabel.setForeground(Color.WHITE);
        cinLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        cinLabel.setBounds(50,400,120,25);

        JTextField cinField = new JTextField();
        cinField.setBounds(50,430,300,35);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(125,500,150,40);
        registerBtn.setBackground(new Color(110,80,210));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI",Font.BOLD,14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());

        card.add(title);
        card.add(nameLabel);
        card.add(nameField);
        card.add(emailLabel);
        card.add(emailField);
        card.add(passLabel);
        card.add(passField);
        card.add(gstLabel);
        card.add(gstField);
        card.add(cinLabel);
        card.add(cinField);
        card.add(registerBtn);

        background.add(card);
        add(background);

        setVisible(true);

        registerBtn.addActionListener(e -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String gst = gstField.getText().trim().toUpperCase();
            String cin = cinField.getText().trim().toUpperCase();

            if(!name.matches("^[A-Za-z ]{3,50}$")){
                JOptionPane.showMessageDialog(this,"Invalid Name!");
                return;
            }

            if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")){
                JOptionPane.showMessageDialog(this,"Invalid Email!");
                return;
            }

            if(!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!]{6,}$")){
                JOptionPane.showMessageDialog(this,
                        "Password must contain letters, numbers and be at least 6 characters!");
                return;
            }

            if(!gst.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z]Z[0-9A-Z]$")){
                JOptionPane.showMessageDialog(this,"Invalid GST Format!");
                return;
            }

            if(!cin.matches("^[LU][0-9]{5}[A-Z]{2}[0-9]{4}[A-Z]{3}[0-9]{6}$")){
                JOptionPane.showMessageDialog(this,"Invalid CIN Format!");
                return;
            }

            try{

                Connection conn = DBConnection.getConnection();

                String sql =
                "INSERT INTO suppliers (name,email,password,gst_number,cin_number,status) VALUES (?,?,?,?,?,?)";

                PreparedStatement pst = conn.prepareStatement(sql);

                pst.setString(1,name);
                pst.setString(2,email);

                String hashedPassword = PasswordUtil.hashPassword(password);
                pst.setString(3,hashedPassword);

                pst.setString(4,gst);
                pst.setString(5,cin);
                pst.setString(6,"pending");

                pst.executeUpdate();

                EmailSender.sendSupplierRegistrationEmail(email);

                JOptionPane.showMessageDialog(this,
                        "Supplier Registered Successfully!\nCheck your email for confirmation.");

                conn.close();

                new LoginSelectionFrame();
                dispose();

            }
            catch(Exception ex){

                ex.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        "Registration Error:\n"+ex.getMessage());

            }

        });

    }
}