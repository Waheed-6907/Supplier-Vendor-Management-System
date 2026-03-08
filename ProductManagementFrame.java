package ui;

import dao.ProductDAO;
import config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementFrame extends JFrame {

    private int supplierId;

    private JTextField txtName, txtPrice, txtStock, txtCustomCategory;
    private JTextArea txtDescription;
    private JComboBox<String> cmbCategory, cmbStatus;

    private ProductDAO dao = new ProductDAO();

    // ===== THEME COLORS =====
    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;
    Color FIELD_COLOR = new Color(70,80,100);

    public ProductManagementFrame(int supplierId) {

        this.supplierId = supplierId;

        setTitle("Product Management");
        setSize(950,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

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

        background.setLayout(new BorderLayout());
        setContentPane(background);

        createUI(background);

        setVisible(true);
    }

    private void createUI(JPanel background) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(40,80,40,80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,15,12,15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Product Management");
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        title.setForeground(TEXT_COLOR);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title,gbc);
        gbc.gridwidth = 1;

        txtName = new JTextField(20);
        txtPrice = new JTextField(20);
        txtStock = new JTextField(20);
        txtCustomCategory = new JTextField(20);

        txtDescription = new JTextArea(4,20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        styleField(txtName);
        styleField(txtPrice);
        styleField(txtStock);
        styleField(txtCustomCategory);
        styleArea(txtDescription);

        txtCustomCategory.setVisible(false);

        cmbCategory = new JComboBox<>(loadCategories());
        cmbStatus = new JComboBox<>(new String[]{"Active","Inactive"});

        styleCombo(cmbCategory);
        styleCombo(cmbStatus);

        cmbCategory.addActionListener(e -> {
            String selected = cmbCategory.getSelectedItem().toString();

            if(selected.equals("None")){
                txtCustomCategory.setVisible(true);
            } else {
                txtCustomCategory.setVisible(false);
            }

            panel.revalidate();
            panel.repaint();
        });

        addLabel(panel,"Product Name:",1,gbc);
        addField(panel,txtName,1,gbc);

        addLabel(panel,"Category:",2,gbc);
        addField(panel,cmbCategory,2,gbc);

        addLabel(panel,"Custom Category:",3,gbc);
        addField(panel,txtCustomCategory,3,gbc);

        addLabel(panel,"Unit Price:",4,gbc);
        addField(panel,txtPrice,4,gbc);

        addLabel(panel,"Stock:",5,gbc);
        addField(panel,txtStock,5,gbc);

        addLabel(panel,"Description:",6,gbc);

        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setPreferredSize(new Dimension(250,100));
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.getViewport().setBackground(FIELD_COLOR);

        addField(panel,descScroll,6,gbc);

        addLabel(panel,"Status:",7,gbc);
        addField(panel,cmbStatus,7,gbc);

        JButton btnAdd = new JButton("Add Product");
        JButton btnEdit = new JButton("Edit Products");
        JButton btnBack = new JButton("Back to Dashboard");

        styleButton(btnAdd);
        styleButton(btnEdit);
        styleButton(btnBack);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(btnAdd,gbc);

        gbc.gridx = 1;
        panel.add(btnEdit,gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(btnBack,gbc);

        background.add(panel,BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addProduct());

        btnEdit.addActionListener(e -> {
            new EditProductFrame(supplierId);
            dispose();
        });

        btnBack.addActionListener(e -> {
            new SupplierDashboardFrame(supplierId);
            dispose();
        });
    }

    private void addProduct() {

        try {

            String name = txtName.getText().trim();

            if(name.isEmpty()){
                JOptionPane.showMessageDialog(this,"Product name cannot be empty!");
                return;
            }

            String category = cmbCategory.getSelectedItem().toString();

            if(category.equals("None")){
                category = txtCustomCategory.getText().trim();

                if(category.isEmpty()){
                    JOptionPane.showMessageDialog(this,"Please enter custom category!");
                    return;
                }
            }

            double price = Double.parseDouble(txtPrice.getText());

            if(price < 0){
                JOptionPane.showMessageDialog(this,"Price cannot be negative!");
                return;
            }

            int stock = Integer.parseInt(txtStock.getText());

            if(stock < 0){
                JOptionPane.showMessageDialog(this,"Stock cannot be negative!");
                return;
            }

            String description = txtDescription.getText();
            String status = cmbStatus.getSelectedItem().toString();

            boolean inserted = dao.addProduct(
                    name,
                    category,
                    price,
                    description,
                    status,
                    supplierId,
                    stock
            );

            if(inserted){
                JOptionPane.showMessageDialog(this,"Product Added Successfully!");
                clearFields();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,"Price and Stock must be numeric!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clearFields(){
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtDescription.setText("");
        txtCustomCategory.setText("");
    }

    private String[] loadCategories(){

        List<String> list = new ArrayList<>();

        try(Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT category_name FROM category")){

            while(rs.next()){
                list.add(rs.getString("category_name"));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        list.add("None");

        return list.toArray(new String[0]);
    }

    private void styleField(JTextField field){
        field.setFont(new Font("Segoe UI",Font.PLAIN,14));
        field.setBackground(FIELD_COLOR);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
    }

    private void styleArea(JTextArea area){
        area.setFont(new Font("Segoe UI",Font.PLAIN,14));
        area.setBackground(FIELD_COLOR);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
    }

    private void styleCombo(JComboBox<String> combo){
        combo.setFont(new Font("Segoe UI",Font.PLAIN,14));
        combo.setBackground(FIELD_COLOR);
        combo.setForeground(Color.WHITE);
    }

    private void styleButton(JButton btn){
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addLabel(JPanel panel,String text,int y,GridBagConstraints gbc){
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("Segoe UI",Font.PLAIN,14));
        panel.add(lbl,gbc);
    }

    private void addField(JPanel panel,Component comp,int y,GridBagConstraints gbc){
        gbc.gridx = 1;
        gbc.gridy = y;
        panel.add(comp,gbc);
    }
}