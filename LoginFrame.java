package ui;

import config.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox showPasswordCheck;

    public LoginFrame() {

        setTitle("Vendor Login");
        setSize(900,650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== BACKGROUND GRADIENT =====

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

        // ===== LOGIN CARD =====

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420,420));
        card.setBackground(new Color(44,52,70));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        // ===== TITLE =====

        JLabel title = new JLabel("Vendor Login",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        title.setForeground(Color.WHITE);

        gbc.gridy=row++;
        gbc.insets = new Insets(20,20,30,20);
        card.add(title,gbc);

        // ===== EMAIL =====

        JLabel emailLabel = createLabel("Email");
        gbc.gridy=row++;
        gbc.insets = new Insets(5,30,5,30);
        card.add(emailLabel,gbc);

        txtEmail = createField();
        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,20,30);
        card.add(txtEmail,gbc);

        // ===== PASSWORD =====

        JLabel passLabel = createLabel("Password");
        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,5,30);
        card.add(passLabel,gbc);

        txtPassword = new JPasswordField();
        styleField(txtPassword);

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,10,30);
        card.add(txtPassword,gbc);

        // ===== SHOW PASSWORD =====

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setForeground(Color.WHITE);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));

        showPasswordCheck.addActionListener(e -> togglePassword());

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,20,30);
        card.add(showPasswordCheck,gbc);

        // ===== LOGIN BUTTON =====

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI",Font.BOLD,15));
        btnLogin.setBackground(new Color(102,75,200));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy=row++;
        gbc.insets = new Insets(0,100,20,100);
        card.add(btnLogin,gbc);

        // ===== FORGOT PASSWORD =====

        JLabel forgotPassword = new JLabel("Forgot Password?",JLabel.CENTER);
        forgotPassword.setForeground(new Color(200,200,200));
        forgotPassword.setFont(new Font("Segoe UI",Font.PLAIN,14));
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy=row++;
        gbc.insets = new Insets(0,0,10,0);
        card.add(forgotPassword,gbc);

        background.add(card);
        add(background);

        // ===== ACTIONS =====

        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());

        forgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ForgotPasswordFrame();
                dispose();
            }
        });

        setVisible(true);
    }

    // ===== PASSWORD TOGGLE =====

    private void togglePassword(){

        if(showPasswordCheck.isSelected()){
            txtPassword.setEchoChar((char)0);
        }else{
            txtPassword.setEchoChar('*');
        }
    }

    // ===== LABEL STYLE =====

    private JLabel createLabel(String text){

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI",Font.PLAIN,14));
        return label;
    }

    // ===== FIELD STYLE =====

    private JTextField createField(){

        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field){

        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setPreferredSize(new Dimension(0,38));
        field.setBackground(new Color(70,80,100));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
    }

    // ===== LOGIN LOGIC =====

    private void login(){

        String emailInput = txtEmail.getText().trim();
        String passwordInput = new String(txtPassword.getPassword());

        if(emailInput.isEmpty() || passwordInput.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please fill all fields");
            return;
        }

        try(Connection con = DBConnection.getConnection()){

            PreparedStatement ps =
                    con.prepareStatement(
                            "SELECT * FROM vendors WHERE LOWER(TRIM(email)) = LOWER(?)"
                    );

            ps.setString(1,emailInput);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                String hashedPassword = rs.getString("password");

                if(BCrypt.checkpw(passwordInput,hashedPassword)){

                    JOptionPane.showMessageDialog(this,"Login Successful!");
                    new VendorDashboard(rs.getInt("id"));
                    dispose();

                }else{
                    JOptionPane.showMessageDialog(this,"Invalid Password");
                }

            }else{
                JOptionPane.showMessageDialog(this,"Email not found");
            }

        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Login Error: "+ex.getMessage());
        }
    }
}