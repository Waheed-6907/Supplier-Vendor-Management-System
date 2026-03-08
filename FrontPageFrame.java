package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrontPageFrame extends JFrame {

    public FrontPageFrame() {

        setTitle("Supplier Vendor Management System");
        setSize(1000,700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== GRADIENT BACKGROUND =====
        JPanel background = new JPanel(new GridBagLayout()) {

            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(15,32,39),
                        0,getHeight(),new Color(44,83,100)
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        // ===== GLASS CARD =====
        JPanel card = new RoundedPanel(30,new Color(255,255,255,20));

        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(560,520));
        card.setMaximumSize(new Dimension(560,520));
        card.setBorder(new EmptyBorder(40,60,40,60));
        card.setOpaque(false);

        // ===== TEXT SECTION =====

        JLabel welcome = new JLabel("Welcome to");
        welcome.setFont(new Font("Segoe UI",Font.PLAIN,18));
        welcome.setForeground(new Color(220,220,220));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(
                "<html><div style='text-align:center;'>Supplier Vendor<br>Management System</div></html>"
        );

        title.setFont(new Font("Segoe UI",Font.BOLD,38));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel desc = new JLabel("Streamlining Vendor and Supplier Collaboration");

        desc.setFont(new Font("Segoe UI",Font.PLAIN,15));
        desc.setForeground(new Color(200,200,200));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("LOGIN AS");

        subtitle.setFont(new Font("Segoe UI",Font.BOLD,20));
        subtitle.setForeground(new Color(220,220,220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel choose = new JLabel("Choose your portal to continue");

        choose.setFont(new Font("Segoe UI",Font.PLAIN,13));
        choose.setForeground(new Color(180,180,180));
        choose.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(welcome);
        card.add(Box.createVerticalStrut(10));

        card.add(title);
        card.add(Box.createVerticalStrut(15));

        card.add(desc);
        card.add(Box.createVerticalStrut(35));

        card.add(subtitle);
        card.add(Box.createVerticalStrut(5));

        card.add(choose);
        card.add(Box.createVerticalStrut(30));

        // ===== BUTTONS =====

        JButton adminBtn = createStyledButton("Admin",new Color(70,160,235));
        JButton supplierBtn = createStyledButton("Supplier",new Color(102,75,200));
        JButton vendorBtn = createStyledButton("Vendor",new Color(40,167,69));

        // ===== BUTTON ACTIONS =====

        adminBtn.addActionListener(e -> {
            dispose();
            new LoginSelectionFrame();
        });

        supplierBtn.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        vendorBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        card.add(adminBtn);
        card.add(Box.createVerticalStrut(20));

        card.add(supplierBtn);
        card.add(Box.createVerticalStrut(20));

        card.add(vendorBtn);

        background.add(card);

        add(background);

        setVisible(true);
    }

    // ===== CUSTOM BUTTON STYLE =====

    private JButton createStyledButton(String text,Color baseColor){

        JButton btn = new JButton(text){

            protected void paintComponent(Graphics g){

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if(getModel().isPressed()){
                    g2.setColor(baseColor.darker());
                }
                else if(getModel().isRollover()){
                    g2.setColor(baseColor.brighter());
                }
                else{
                    g2.setColor(baseColor);
                }

                g2.fillRoundRect(0,0,getWidth(),getHeight(),15,15);

                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(360,50));
        btn.setFont(new Font("Segoe UI",Font.BOLD,16));
        btn.setForeground(Color.WHITE);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    // ===== ROUNDED PANEL =====

    class RoundedPanel extends JPanel {

        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius,Color bgColor){
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
        }

        protected void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);

            g2.fillRoundRect(
                    0,0,getWidth()-1,getHeight()-1,
                    cornerRadius,cornerRadius
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FrontPageFrame::new);
    }
}