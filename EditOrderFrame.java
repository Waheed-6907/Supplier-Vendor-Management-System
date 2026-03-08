package ui;

import config.DBConnection;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class EditOrderFrame extends JFrame {

    private JComboBox<String> productBox;
    private JTextField unitsField;
    private JDateChooser dateChooser;

    private int orderId;
    private int vendorId;

    public EditOrderFrame(int orderId, int vendorId) {

        this.orderId = orderId;
        this.vendorId = vendorId;

        setTitle("Edit Order");
        setSize(650,550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ===== DARK TEAL BACKGROUND =====
        JPanel background = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(5,55,70),
                        0,getHeight(),new Color(10,95,95)
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        background.setLayout(new GridBagLayout());

        // ===== CARD PANEL =====
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(450,420));
        card.setBackground(new Color(44,52,70));
        card.setBorder(new EmptyBorder(35,45,35,45));

        JLabel title = new JLabel("Edit Order");
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0,0,25,0));

        card.add(title,BorderLayout.NORTH);

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(6,1,12,10));
        form.setOpaque(false);

        productBox = new JComboBox<>();
        unitsField = new JTextField();

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        styleField(productBox);
        styleField(unitsField);
        styleField(dateChooser);

        form.add(createLabel("Product"));
        form.add(productBox);

        form.add(createLabel("Units"));
        form.add(unitsField);

        form.add(createLabel("Delivery Date"));
        form.add(dateChooser);

        card.add(form,BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton updateBtn = createThemeButton("Update Order");
        updateBtn.addActionListener(e -> updateOrder());

        buttonPanel.add(updateBtn);
        card.add(buttonPanel,BorderLayout.SOUTH);

        background.add(card);
        add(background);

        loadOrderData();

        setVisible(true);
    }

    // ================= LOAD ORDER =================

    private void loadOrderData(){

        try(Connection con = DBConnection.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "SELECT o.product_id,o.units,o.delivery_date,p.category_id " +
                    "FROM v_orders o " +
                    "JOIN product p ON o.product_id=p.product_id " +
                    "WHERE o.order_id=?"
            );

            ps.setInt(1,orderId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                int currentProductId = rs.getInt("product_id");
                int categoryId = rs.getInt("category_id");

                unitsField.setText(rs.getString("units"));
                dateChooser.setDate(rs.getDate("delivery_date"));

                loadProducts(categoryId,currentProductId);
            }

        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error loading order data");
        }
    }

    private void loadProducts(int categoryId,int selectedProductId){

        try(Connection con = DBConnection.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "SELECT product_id,product_name FROM product WHERE category_id=?"
            );

            ps.setInt(1,categoryId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                int pid = rs.getInt("product_id");
                String name = rs.getString("product_name");

                String item = pid+"-"+name;
                productBox.addItem(item);

                if(pid == selectedProductId){
                    productBox.setSelectedItem(item);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= UPDATE ORDER =================

    private void updateOrder(){

        try{

            if(productBox.getSelectedItem()==null){
                JOptionPane.showMessageDialog(this,"Select product");
                return;
            }

            if(unitsField.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(this,"Enter units");
                return;
            }

            Date selectedDate = dateChooser.getDate();

            if(selectedDate==null){
                JOptionPane.showMessageDialog(this,"Select delivery date");
                return;
            }

            int productId = Integer.parseInt(
                    productBox.getSelectedItem().toString().split("-")[0]
            );

            int units = Integer.parseInt(unitsField.getText().trim());

            java.sql.Date sqlDate =
                    new java.sql.Date(selectedDate.getTime());

            try(Connection con = DBConnection.getConnection()){

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE v_orders SET product_id=?,units=?,delivery_date=? " +
                        "WHERE order_id=? AND vendor_id=?"
                );

                ps.setInt(1,productId);
                ps.setInt(2,units);
                ps.setDate(3,sqlDate);
                ps.setInt(4,orderId);
                ps.setInt(5,vendorId);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,"Order Updated Successfully!");

            dispose();
            new MyOrdersFrame(vendorId);

        }catch(NumberFormatException e){

            JOptionPane.showMessageDialog(this,"Units must be numeric!");

        }catch(Exception e){

            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Something went wrong!");
        }
    }

    // ================= UI HELPERS =================

    private JLabel createLabel(String text){

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI",Font.BOLD,14));
        label.setForeground(Color.WHITE);

        return label;
    }

    private void styleField(JComponent comp){

        comp.setFont(new Font("Segoe UI",Font.PLAIN,14));
        comp.setPreferredSize(new Dimension(200,35));
    }

    private JButton createThemeButton(String text){

        JButton btn = new JButton(text);

        btn.setBackground(new Color(102,75,200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10,25,10,25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));

        btn.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseEntered(java.awt.event.MouseEvent evt){
                btn.setBackground(new Color(120,95,220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt){
                btn.setBackground(new Color(102,75,200));
            }
        });

        return btn;
    }
}