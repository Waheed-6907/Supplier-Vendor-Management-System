package ui;

import config.DBConnection;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class PaymentFrame extends JFrame {

    private int vendorId;
    private int orderId;

    private double price;
    private int units;

    public PaymentFrame(int vendorId, int orderId) {

        this.vendorId = vendorId;
        this.orderId = orderId;

        fetchOrderDetails();

        setTitle("Payment");
        setSize(550,500);
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
        card.setPreferredSize(new Dimension(420,420));
        card.setBackground(new Color(44,52,70));
        card.setBorder(new EmptyBorder(30,40,30,40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        JLabel title = new JLabel("Payment Details",SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,22));
        title.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        card.add(title,gbc);

        gbc.gridwidth = 1;

        JLabel priceLabel = new JLabel("Unit Price:");
        priceLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(priceLabel,gbc);

        JLabel priceValue = new JLabel("Rs. "+price);
        priceValue.setForeground(Color.WHITE);

        gbc.gridx = 1;
        card.add(priceValue,gbc);
        row++;

        JLabel unitsLabel = new JLabel("Units Ordered:");
        unitsLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(unitsLabel,gbc);

        JLabel unitsValue = new JLabel(String.valueOf(units));
        unitsValue.setForeground(Color.WHITE);

        gbc.gridx = 1;
        card.add(unitsValue,gbc);
        row++;

        double totalAmount = units * price;

        JLabel totalLabel = new JLabel("Total Amount:");
        totalLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(totalLabel,gbc);

        JLabel totalValue = new JLabel("Rs. "+totalAmount);
        totalValue.setForeground(Color.WHITE);

        gbc.gridx = 1;
        card.add(totalValue,gbc);
        row++;

        JLabel deliveryLabel = new JLabel("Delivery Date:");
        deliveryLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(deliveryLabel,gbc);

        JDateChooser deliveryChooser = new JDateChooser();
        deliveryChooser.setDateFormatString("yyyy-MM-dd");
        deliveryChooser.setDate(new Date());

        gbc.gridx = 1;
        card.add(deliveryChooser,gbc);
        row++;

        JLabel paymentDateText = new JLabel("Payment Date:");
        paymentDateText.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(paymentDateText,gbc);

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        JLabel paymentDateValue = new JLabel(today.toString());
        paymentDateValue.setForeground(Color.WHITE);

        gbc.gridx = 1;
        card.add(paymentDateValue,gbc);
        row++;

        JLabel statusLabel = new JLabel("Payment Status:");
        statusLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = row;
        card.add(statusLabel,gbc);

        String[] statusOptions = {"PAID","PROCESSING","FAILED"};
        JComboBox<String> statusBox = new JComboBox<>(statusOptions);

        gbc.gridx = 1;
        card.add(statusBox,gbc);
        row++;

        JButton submit = new JButton("Submit Payment");
        styleButton(submit);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        card.add(submit,gbc);

        submit.addActionListener(e ->
                processPayment(deliveryChooser,statusBox,totalAmount)
        );

        background.add(card);
        add(background);

        setVisible(true);
    }

    private void fetchOrderDetails(){

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT o.units, p.unit_price " +
                    "FROM v_orders o " +
                    "JOIN product p ON o.product_id=p.product_id " +
                    "WHERE o.order_id=?"
            );

            ps.setInt(1,orderId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                units = rs.getInt("units");
                price = rs.getDouble("unit_price");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void processPayment(JDateChooser deliveryChooser,
                                JComboBox<String> statusBox,
                                double amount){

        Connection con = null;

        try{

            Date deliveryDate = deliveryChooser.getDate();

            if(deliveryDate == null){
                JOptionPane.showMessageDialog(this,
                        "Please select delivery date");
                return;
            }

            java.sql.Date deliverySql =
                    new java.sql.Date(deliveryDate.getTime());

            java.sql.Date paymentSql =
                    new java.sql.Date(System.currentTimeMillis());

            String payStatus =
                    statusBox.getSelectedItem().toString();

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            PreparedStatement payPs = con.prepareStatement(
                    "INSERT INTO payment (order_id,amount,payment_date,payment_status) VALUES (?,?,?,?)"
            );

            payPs.setInt(1,orderId);
            payPs.setDouble(2,amount);
            payPs.setDate(3,paymentSql);
            payPs.setString(4,payStatus);

            payPs.executeUpdate();

            PreparedStatement deliveryUpdate = con.prepareStatement(
                    "UPDATE v_orders SET delivery_date=? WHERE order_id=?"
            );

            deliveryUpdate.setDate(1,deliverySql);
            deliveryUpdate.setInt(2,orderId);
            deliveryUpdate.executeUpdate();

            PreparedStatement getOrder = con.prepareStatement(
                    "SELECT product_id, units FROM v_orders WHERE order_id=?"
            );

            getOrder.setInt(1,orderId);

            ResultSet rs = getOrder.executeQuery();

            if(rs.next()){

                int productId = rs.getInt("product_id");
                int units = rs.getInt("units");

                PreparedStatement stockUpdate = con.prepareStatement(
                        "UPDATE product SET stock = stock - ? WHERE product_id=?"
                );

                stockUpdate.setInt(1,units);
                stockUpdate.setInt(2,productId);

                stockUpdate.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(this,"Payment Successful!");

            dispose();
            new MyOrdersFrame(vendorId);

        }catch(Exception ex){

            try{
                if(con!=null) con.rollback();
            }catch(Exception ignored){}

            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Payment Failed: "+ex.getMessage());
        }
    }

    private void styleButton(JButton btn){

        btn.setBackground(new Color(102,75,200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));
    }
}