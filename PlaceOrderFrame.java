package ui;
import config.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class PlaceOrderFrame extends JFrame {

    private JComboBox<String> categoryBox;
    private JComboBox<String> productBox;
    private JTextField unitsField;
    private com.toedter.calendar.JDateChooser dateChooser;
    private int vendorId;

    public PlaceOrderFrame(int vendorId) {

        this.vendorId = vendorId;

        setTitle("Create New Order");
        setSize(900,700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(500,560));
        card.setBackground(new Color(44,52,70));
        card.setBorder(new EmptyBorder(40,60,40,60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        JLabel title = new JLabel("Create New Order",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        title.setForeground(Color.WHITE);

        gbc.gridy=row++;
        gbc.insets=new Insets(0,0,30,0);
        card.add(title,gbc);

        addLabel(card,gbc,row++,"Category");

        categoryBox=new JComboBox<>();
        categoryBox.addItem("Select Category");
        loadCategories();
        styleField(categoryBox);
        addField(card,gbc,row++,categoryBox);

        addLabel(card,gbc,row++,"Product");

        productBox=new JComboBox<>();
        productBox.addItem("Select Product");
        styleField(productBox);
        addField(card,gbc,row++,productBox);

        categoryBox.addActionListener(e->{
            if(categoryBox.getSelectedIndex()>0){
                int categoryId=Integer.parseInt(
                        categoryBox.getSelectedItem().toString().split("-")[0]
                );
                loadProducts(categoryId);
            }
        });

        addLabel(card,gbc,row++,"Quantity / Units");

        unitsField=new JTextField();
        styleField(unitsField);
        addField(card,gbc,row++,unitsField);

        addLabel(card,gbc,row++,"Delivery Date");

        dateChooser = new com.toedter.calendar.JDateChooser();
dateChooser.setDateFormatString("yyyy-MM-dd");
dateChooser.setDate(new Date());
styleField(dateChooser);
addField(card, gbc, row++, dateChooser);

        JPanel btnPanel=new JPanel(new GridLayout(1,2,15,0));
        btnPanel.setOpaque(false);

        JButton placeBtn=createThemeButton("Place Order");
        JButton cancelBtn=createThemeButton("Cancel");

        btnPanel.add(placeBtn);
        btnPanel.add(cancelBtn);

        gbc.gridy=row++;
        gbc.insets=new Insets(35,0,0,0);
        card.add(btnPanel,gbc);

        background.add(card);
        add(background);

        placeBtn.addActionListener(e->handleOrderPlacement());
        cancelBtn.addActionListener(e->dispose());

        setVisible(true);
    }

    private void handleOrderPlacement(){

        try{

            if(productBox.getSelectedIndex()==0){
                JOptionPane.showMessageDialog(this,"Select product");
                return;
            }

            int productId=Integer.parseInt(productBox.getSelectedItem().toString().split("-")[0]);
            int units=Integer.parseInt(unitsField.getText().trim());

            if(units<=0){
                JOptionPane.showMessageDialog(this,"Units must be > 0");
                return;
            }

            Date selectedDate = dateChooser.getDate();
            if(selectedDate == null){
    JOptionPane.showMessageDialog(this,"Please select delivery date");
    return;
}
            java.sql.Date sqlDate=new java.sql.Date(selectedDate.getTime());

            Connection con=DBConnection.getConnection();

            PreparedStatement stockPs=con.prepareStatement(
                    "SELECT stock FROM product WHERE product_id=?"
            );

            stockPs.setInt(1,productId);
            ResultSet rs=stockPs.executeQuery();

            int stock=0;
            if(rs.next()){
                stock=rs.getInt("stock");
            }

            if(units>stock){
                JOptionPane.showMessageDialog(this,"Insufficient stock");
                return;
            }

            PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO v_orders (vendor_id,product_id,units,delivery_date,status,is_deleted) VALUES (?,?,?,?, 'PENDING',0)"
            );

            ps.setInt(1,vendorId);
            ps.setInt(2,productId);
            ps.setInt(3,units);
            ps.setDate(4,sqlDate);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Order placed successfully");

            dispose();
            new MyOrdersFrame(vendorId);

        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadCategories(){
        try(Connection con=DBConnection.getConnection()){
            ResultSet rs=con.createStatement()
                    .executeQuery("SELECT category_id,category_name FROM category");

            while(rs.next()){
                categoryBox.addItem(rs.getInt("category_id")+"-"+rs.getString("category_name"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadProducts(int categoryId){

        productBox.removeAllItems();
        productBox.addItem("Select Product");

        try(Connection con=DBConnection.getConnection()){

            PreparedStatement ps=con.prepareStatement(
                    "SELECT product_id,product_name FROM product WHERE category_id=?"
            );

            ps.setInt(1,categoryId);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                productBox.addItem(rs.getInt("product_id")+"-"+rs.getString("product_name"));
            }

        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());}
    }

    private void addLabel(JPanel panel,GridBagConstraints gbc,int row,String text){
        JLabel label=new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI",Font.PLAIN,14));
        gbc.gridy=row;
        panel.add(label,gbc);
    }

    private void addField(JPanel panel,GridBagConstraints gbc,int row,JComponent comp){
        gbc.gridy=row;
        comp.setPreferredSize(new Dimension(0,40));
        panel.add(comp,gbc);
    }

    private void styleField(JComponent comp){
        comp.setPreferredSize(new Dimension(0,40));
    }

    private JButton createThemeButton(String text){

        JButton btn=new JButton(text);

        btn.setBackground(new Color(102,75,200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));

        return btn;
    }
}