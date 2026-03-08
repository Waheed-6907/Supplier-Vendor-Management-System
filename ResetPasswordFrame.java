package ui;

import config.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ResetPasswordFrame extends JFrame {

    private int vendorId;

    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    public ResetPasswordFrame(int vendorId){

        this.vendorId = vendorId;

        setTitle("Reset Password");
        setSize(900,650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420,380));
        card.setBackground(new Color(44,52,70));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        JLabel title = new JLabel("Reset Password",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(Color.WHITE);

        gbc.gridy=row++;
        gbc.insets = new Insets(20,20,30,20);
        card.add(title,gbc);

        JLabel passLabel = createLabel("New Password");

        gbc.gridy=row++;
        gbc.insets = new Insets(5,30,5,30);
        card.add(passLabel,gbc);

        txtNewPassword = createPassword();

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,20,30);
        card.add(txtNewPassword,gbc);

        JLabel confirmLabel = createLabel("Confirm Password");

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,5,30);
        card.add(confirmLabel,gbc);

        txtConfirmPassword = createPassword();

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,20,30);
        card.add(txtConfirmPassword,gbc);

        JButton resetBtn = new JButton("Reset Password");

        resetBtn.setFont(new Font("Segoe UI",Font.BOLD,15));
        resetBtn.setBackground(new Color(102,75,200));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        gbc.gridy=row++;
        gbc.insets = new Insets(0,100,20,100);
        card.add(resetBtn,gbc);

        background.add(card);
        add(background);

        resetBtn.addActionListener(e -> resetPassword());

        setVisible(true);
    }

    private JLabel createLabel(String text){

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI",Font.PLAIN,14));
        return label;
    }

    private JPasswordField createPassword(){

        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setPreferredSize(new Dimension(0,38));
        field.setBackground(new Color(70,80,100));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));

        return field;
    }

    private void resetPassword(){

        String pass1 = new String(txtNewPassword.getPassword());
        String pass2 = new String(txtConfirmPassword.getPassword());

        if(pass1.isEmpty() || pass2.isEmpty()){

            JOptionPane.showMessageDialog(this,"Please fill all fields");
            return;
        }

        if(!pass1.equals(pass2)){

            JOptionPane.showMessageDialog(this,"Passwords do not match");
            return;
        }

        try(Connection con = DBConnection.getConnection()){

            String hashed = BCrypt.hashpw(pass1,BCrypt.gensalt());

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE vendors SET password=? WHERE id=?"
            );

            ps.setString(1,hashed);
            ps.setInt(2,vendorId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Password Reset Successful");

            new LoginFrame();
            dispose();

        }catch(Exception ex){

            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }
}