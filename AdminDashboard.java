package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(1200,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== MAIN BACKGROUND =====
        JPanel mainBackground = new JPanel(new BorderLayout());
        mainBackground.setBackground(new Color(8,35,45));

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240,700));
        sidebar.setBackground(new Color(40,45,65));
        sidebar.setLayout(new BorderLayout());

        JLabel title = new JLabel("  ADMIN PANEL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,20));
        title.setBorder(BorderFactory.createEmptyBorder(25,15,25,15));

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(40,45,65));
        menuPanel.setLayout(new GridLayout(6,1,0,10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));

        JButton vendorsBtn = createMenuButton("Registered Vendors");
        JButton vendorActivityBtn = createMenuButton("Vendor Activity");
        JButton suppliersBtn = createMenuButton("Registered Suppliers");
        JButton supplierActivityBtn = createMenuButton("Supplier Activity");
        JButton pendingAdminsBtn = createMenuButton("Pending Admin Requests");
        JButton logoutBtn = createMenuButton("Logout");

        menuPanel.add(vendorsBtn);
        menuPanel.add(vendorActivityBtn);
        menuPanel.add(suppliersBtn);
        menuPanel.add(supplierActivityBtn);
        menuPanel.add(pendingAdminsBtn);
        menuPanel.add(logoutBtn);

        sidebar.add(title,BorderLayout.NORTH);
        sidebar.add(menuPanel,BorderLayout.CENTER);

        // ===== CONTENT AREA =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(30,35,55));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        contentPanel.add(new RegisteredVendorsPanel(),"vendors");
        contentPanel.add(new VendorActivityPanel(),"vendorActivity");
        contentPanel.add(new RegisteredSuppliersPanel(),"suppliers");
        contentPanel.add(new SupplierActivityPanel(),"supplierActivity");
        contentPanel.add(new PendingAdminsPanel(),"pendingAdmins");

        mainBackground.add(sidebar,BorderLayout.WEST);
        mainBackground.add(contentPanel,BorderLayout.CENTER);

        add(mainBackground);

        // ===== BUTTON ACTIONS =====

        vendorsBtn.addActionListener(e -> 
            cardLayout.show(contentPanel,"vendors")
        );

        vendorActivityBtn.addActionListener(e -> 
            cardLayout.show(contentPanel,"vendorActivity")
        );

        suppliersBtn.addActionListener(e -> 
            cardLayout.show(contentPanel,"suppliers")
        );

        supplierActivityBtn.addActionListener(e -> 
            cardLayout.show(contentPanel,"supplierActivity")
        );

        pendingAdminsBtn.addActionListener(e -> {

            contentPanel.removeAll();

            contentPanel.add(new RegisteredVendorsPanel(),"vendors");
            contentPanel.add(new VendorActivityPanel(),"vendorActivity");
            contentPanel.add(new RegisteredSuppliersPanel(),"suppliers");
            contentPanel.add(new SupplierActivityPanel(),"supplierActivity");
            contentPanel.add(new PendingAdminsPanel(),"pendingAdmins");

            cardLayout.show(contentPanel,"pendingAdmins");

            contentPanel.revalidate();
            contentPanel.repaint();
        });

        logoutBtn.addActionListener(e -> {
            new LoginSelectionFrame();
            dispose();
        });

        setVisible(true);
    }

    // ===== MODERN PURPLE BUTTON STYLE =====
    private JButton createMenuButton(String text){

        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(105,75,190));
        button.setFont(new Font("Segoe UI",Font.BOLD,14));
        button.setBorder(BorderFactory.createEmptyBorder(12,18,12,18));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter(){

            public void mouseEntered(MouseEvent evt){
                button.setBackground(new Color(130,95,220));
            }

            public void mouseExited(MouseEvent evt){
                button.setBackground(new Color(105,75,190));
            }
        });

        return button;
    }
}