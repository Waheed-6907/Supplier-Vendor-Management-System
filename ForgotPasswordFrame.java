package ui;

import config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPasswordFrame extends JFrame {

    private JTextField txtEmail;
    private JTextField txtGST;

    public ForgotPasswordFrame(){

        setTitle("Verify Account");
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

        JLabel title = new JLabel("Account Verification",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(Color.WHITE);

        gbc.gridy=row++;
        gbc.insets = new Insets(20,20,30,20);
        card.add(title,gbc);

        JLabel emailLabel = createLabel("Registered Email");

        gbc.gridy=row++;
        gbc.insets = new Insets(5,30,5,30);
        card.add(emailLabel,gbc);

        txtEmail = createField();

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,20,30);
        card.add(txtEmail,gbc);

        JLabel gstLabel = createLabel("GST Number");

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,5,30);
        card.add(gstLabel,gbc);

        txtGST = createField();

        gbc.gridy=row++;
        gbc.insets = new Insets(0,30,20,30);
        card.add(txtGST,gbc);

        JButton verifyBtn = new JButton("Verify");

        verifyBtn.setFont(new Font("Segoe UI",Font.BOLD,15));
        verifyBtn.setBackground(new Color(102,75,200));
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setFocusPainted(false);
        verifyBtn.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        gbc.gridy=row++;
        gbc.insets = new Insets(0,120,20,120);
        card.add(verifyBtn,gbc);

        background.add(card);
        add(background);

        verifyBtn.addActionListener(e -> verify());

        setVisible(true);
    }

    private JLabel createLabel(String text){

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI",Font.PLAIN,14));
        return label;
    }

    private JTextField createField(){

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setPreferredSize(new Dimension(0,38));
        field.setBackground(new Color(70,80,100));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
        return field;
    }

    private void verify(){

        String email = txtEmail.getText().trim();
        String gst = txtGST.getText().trim();

        if(email.isEmpty() || gst.isEmpty()){

            JOptionPane.showMessageDialog(this,"Please fill all fields");
            return;
        }

        try(Connection con = DBConnection.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM vendors WHERE email=? AND gst_number=?"
            );

            ps.setString(1,email);
            ps.setString(2,gst);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                new ResetPasswordFrame(rs.getInt("id"));
                dispose();

            }else{

                JOptionPane.showMessageDialog(this,"Verification failed");
            }

        }catch(Exception ex){

            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }
}