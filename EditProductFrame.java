package ui;

import dao.ProductDAO;
import config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditProductFrame extends JFrame {

    private int supplierId;
    private int selectedProductId = -1;

    private JTextField txtName, txtPrice, txtCustomCategory, txtStock;
    private JTextArea txtDescription;
    private JComboBox<String> cmbCategory, cmbStatus;

    private JPanel productListPanel;

    private ProductDAO dao = new ProductDAO();

    // ===== THEME COLORS =====
    Color BACKGROUND_START = new Color(5,55,70);
    Color BACKGROUND_END = new Color(10,95,95);
    Color CARD_COLOR = new Color(44,52,70);
    Color BUTTON_COLOR = new Color(102,75,200);
    Color TEXT_COLOR = Color.WHITE;
    Color FIELD_COLOR = new Color(70,80,100);

    public EditProductFrame(int supplierId) {

        this.supplierId = supplierId;

        setTitle("Edit Products");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== GRADIENT BACKGROUND =====
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
        background.setLayout(new BorderLayout(15,15));
        setContentPane(background);

        createTopForm(background);
        createProductList(background);

        setVisible(true);
    }

    private void createTopForm(JPanel background) {

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR),
                "Edit Product",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField(20);
        txtPrice = new JTextField(20);
        txtStock = new JTextField(20);
        txtDescription = new JTextArea(3, 20);
        txtCustomCategory = new JTextField(20);
        txtCustomCategory.setVisible(false);

        styleField(txtName);
        styleField(txtPrice);
        styleField(txtStock);
        styleField(txtCustomCategory);
        styleArea(txtDescription);

        cmbCategory = new JComboBox<>(loadCategories());
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        styleCombo(cmbCategory);
        styleCombo(cmbStatus);

        JButton btnUpdate = new JButton("Update Product");
        JButton btnBack = new JButton("Back to Products");

        styleButton(btnUpdate);
        styleButton(btnBack);

        cmbCategory.addActionListener(e -> {
            if (cmbCategory.getSelectedItem().toString().equals("None")) {
                txtCustomCategory.setVisible(true);
            } else {
                txtCustomCategory.setVisible(false);
            }
        });

        addLabel(formPanel, "Product Name:", 0, gbc);
        addField(formPanel, txtName, 0, gbc);

        addLabel(formPanel, "Price:", 1, gbc);
        addField(formPanel, txtPrice, 1, gbc);

        addLabel(formPanel, "Stock:", 2, gbc);
        addField(formPanel, txtStock, 2, gbc);

        addLabel(formPanel, "Description:", 3, gbc);
        addField(formPanel, new JScrollPane(txtDescription), 3, gbc);

        addLabel(formPanel, "Category:", 4, gbc);
        addField(formPanel, cmbCategory, 4, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(txtCustomCategory, gbc);

        addLabel(formPanel, "Status:", 6, gbc);
        addField(formPanel, cmbStatus, 6, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(btnUpdate, gbc);

        gbc.gridx = 1;
        formPanel.add(btnBack, gbc);

        background.add(formPanel, BorderLayout.NORTH);

        btnUpdate.addActionListener(e -> updateProduct());

        btnBack.addActionListener(e -> {
            new ProductManagementFrame(supplierId);
            dispose();
        });
    }

    private void createProductList(JPanel background) {

        productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(productListPanel);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR),
                "Your Products",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
        ));
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setOpaque(false);

        background.add(scroll, BorderLayout.CENTER);

        loadProducts();
    }

    private void loadProducts() {

        productListPanel.removeAll();

        List<Object[]> products = dao.getProductsBySupplier(supplierId);

        for (Object[] p : products) {

            int productId = (int) p[0];
            String name = (String) p[1];
            String category = (String) p[2];
            double price = (double) p[3];
            String description = (String) p[4];
            String status = (String) p[5];
            int stock = (int) p[6];

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            row.setBackground(CARD_COLOR);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BUTTON_COLOR),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            JLabel lbl = new JLabel(
                    "<html><b>" + name + "</b> | " +
                            category + " | ₹" + price +
                            " | Stock: " + stock +
                            " | " + status + "</html>"
            );
            lbl.setForeground(TEXT_COLOR);

            JButton btnEdit = new JButton("Edit");
            styleButton(btnEdit);

            btnEdit.addActionListener(e -> {
                selectedProductId = productId;
                txtName.setText(name);
                txtPrice.setText(String.valueOf(price));
                txtStock.setText(String.valueOf(stock));
                txtDescription.setText(description);

                if (isCategoryInList(category)) {
                    cmbCategory.setSelectedItem(category);
                    txtCustomCategory.setVisible(false);
                } else {
                    cmbCategory.setSelectedItem("None");
                    txtCustomCategory.setText(category);
                    txtCustomCategory.setVisible(true);
                }

                cmbStatus.setSelectedItem(status);
            });

            row.add(lbl, BorderLayout.CENTER);
            row.add(btnEdit, BorderLayout.EAST);

            productListPanel.add(row);
            productListPanel.add(Box.createVerticalStrut(10));
        }

        productListPanel.revalidate();
        productListPanel.repaint();
    }

    private boolean isCategoryInList(String category) {
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            if (cmbCategory.getItemAt(i).equals(category)) {
                return true;
            }
        }
        return false;
    }

    private void updateProduct() {

        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(this, "Select product first!");
            return;
        }

        try {
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            String description = txtDescription.getText();
            String category = cmbCategory.getSelectedItem().toString();

            if (category.equals("None")) {
                category = txtCustomCategory.getText();
            }

            String status = cmbStatus.getSelectedItem().toString();

            boolean updated = dao.updateProduct(
                    selectedProductId,
                    name,
                    description,
                    price,
                    category,
                    status,
                    stock
            );

            if (updated) {
                JOptionPane.showMessageDialog(this, "Product Updated!");
                selectedProductId = -1;
                clearForm();
                loadProducts();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Data!");
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtDescription.setText("");
        txtCustomCategory.setText("");
        txtCustomCategory.setVisible(false);
    }

    private String[] loadCategories() {

        List<String> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT category_name FROM category")) {

            while (rs.next()) {
                list.add(rs.getString("category_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        list.add("None");
        return list.toArray(new String[0]);
    }

    // ===== STYLE METHODS =====

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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addLabel(JPanel panel, String text, int y, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("Segoe UI",Font.PLAIN,14));
        panel.add(lbl, gbc);
    }

    private void addField(JPanel panel, Component comp, int y, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }
}