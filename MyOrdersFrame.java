package ui;

import config.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class MyOrdersFrame extends JFrame {

    private JTable table;
    private int vendorId;
    private TableRowSorter<DefaultTableModel> sorter;

    public MyOrdersFrame(int vendorId) {

        this.vendorId = vendorId;

        setTitle("Order History");
        setSize(1200,750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(1050,600));
        card.setBackground(new Color(44,52,70));
        card.setBorder(new EmptyBorder(30,40,30,40));

        JPanel headerContainer = new JPanel();
        headerContainer.setLayout(new BoxLayout(headerContainer,BoxLayout.Y_AXIS));
        headerContainer.setOpaque(false);

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);

        JLabel title = new JLabel("Order History");
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(Color.WHITE);

        JButton refreshBtn = createThemeButton("Refresh");

        refreshBtn.addActionListener(e -> {
            dispose();
            new MyOrdersFrame(vendorId);
        });

        row1.add(title,BorderLayout.WEST);
        row1.add(refreshBtn,BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        row2.setBorder(new EmptyBorder(15,0,0,0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        leftPanel.setOpaque(false);

        JTextField searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(180,35));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){

            public void insertUpdate(javax.swing.event.DocumentEvent e){ filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e){ filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ filter(); }

            private void filter(){

                String text = searchField.getText().trim();

                if(text.isEmpty()){
                    sorter.setRowFilter(null);
                }else{
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)"+text,2));
                }
            }
        });

        String[] sortOptions = {
                "Sort By",
                "Product Name (A-Z)",
                "Units (Low-High)",
                "Units (High-Low)",
                "Delivery Date (Newest)",
                "Delivery Date (Oldest)"
        };

        JComboBox<String> sortBox = new JComboBox<>(sortOptions);
        sortBox.setPreferredSize(new Dimension(200,35));

        sortBox.addActionListener(e -> {

            String selected = sortBox.getSelectedItem().toString();

            java.util.List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();

            switch(selected){

                case "Product Name (A-Z)":
                    sortKeys.add(new RowSorter.SortKey(2,SortOrder.ASCENDING));
                    break;

                case "Units (Low-High)":
                    sortKeys.add(new RowSorter.SortKey(3,SortOrder.ASCENDING));
                    break;

                case "Units (High-Low)":
                    sortKeys.add(new RowSorter.SortKey(3,SortOrder.DESCENDING));
                    break;

                case "Delivery Date (Newest)":
                    sortKeys.add(new RowSorter.SortKey(4,SortOrder.DESCENDING));
                    break;

                case "Delivery Date (Oldest)":
                    sortKeys.add(new RowSorter.SortKey(4,SortOrder.ASCENDING));
                    break;

                default:
                    sorter.setSortKeys(null);
                    return;
            }

            sorter.setSortKeys(sortKeys);
        });

        leftPanel.add(searchField);
        leftPanel.add(sortBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        buttonPanel.setOpaque(false);

        JButton createBtn = createThemeButton("Create Order");
        JButton viewOrdersBtn = createThemeButton("View Orders");
        JButton backBtn = createThemeButton("Back to Dashboard");

        createBtn.addActionListener(e -> {
            new PlaceOrderFrame(vendorId);
            dispose();
        });

        viewOrdersBtn.addActionListener(e -> new VendorOrdersFrame(vendorId));

        backBtn.addActionListener(e -> {
            new VendorDashboard(vendorId);
            dispose();
        });

        buttonPanel.add(createBtn);
        buttonPanel.add(viewOrdersBtn);
        buttonPanel.add(backBtn);

        row2.add(leftPanel,BorderLayout.WEST);
        row2.add(buttonPanel,BorderLayout.EAST);

        headerContainer.add(row1);
        headerContainer.add(row2);

        card.add(headerContainer,BorderLayout.NORTH);

        table = new JTable();
        styleTable(table);
        loadTableData();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll,BorderLayout.CENTER);

        background.add(card);
        add(background);

        setVisible(true);
    }

    private void loadTableData(){

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT o.order_id, c.category_name, p.product_name, " +
                    "o.units, o.delivery_date, o.status, " +
                    "CASE WHEN pay.payment_id IS NULL THEN 'NO' ELSE 'YES' END AS paid " +
                    "FROM v_orders o " +
                    "JOIN product p ON o.product_id = p.product_id " +
                    "JOIN category c ON p.category_id = c.category_id " +
                    "LEFT JOIN payment pay ON o.order_id = pay.order_id " +
                    "WHERE o.vendor_id=? AND o.is_deleted=0"
            );

            ps.setInt(1,vendorId);

            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID","Category","Product","Units","Delivery Date","Status","Paid","Pay","Edit","Delete"},0){

                public boolean isCellEditable(int row,int column){
                    return column == 7 || column == 8 || column == 9;
                }
            };

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("category_name"),
                        rs.getString("product_name"),
                        rs.getInt("units"),
                        rs.getDate("delivery_date"),
                        rs.getString("status"),
                        rs.getString("paid"),
                        "Pay","Edit","Delete"
                });
            }

            table.setModel(model);

            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);

            table.getColumnModel().getColumn(6).setMinWidth(0);
            table.getColumnModel().getColumn(6).setMaxWidth(0);

            table.getColumn("Pay").setCellRenderer(new ButtonRenderer("Pay"));
            table.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
            table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

            table.getColumn("Pay").setCellEditor(new ButtonEditor("Pay"));
            table.getColumn("Edit").setCellEditor(new ButtonEditor("Edit"));
            table.getColumn("Delete").setCellEditor(new ButtonEditor("Delete"));

            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            applyRowColors();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void deleteOrder(int orderId){

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE v_orders SET is_deleted=1 WHERE order_id=?"
            );

            ps.setInt(1,orderId);
            ps.executeUpdate();

            dispose();
            new MyOrdersFrame(vendorId);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private JButton createThemeButton(String text){

        JButton btn = new JButton(text);

        btn.setBackground(new Color(102,75,200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150,35));
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));

        return btn;
    }

    private void styleTable(JTable table){

        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();

        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setBackground(new Color(102,75,200));
        header.setForeground(Color.WHITE);
    }
private void applyRowColors(){

    table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){

        @Override
        public Component getTableCellRendererComponent(
                JTable table,Object value,boolean isSelected,
                boolean hasFocus,int row,int column){

            Component c = super.getTableCellRendererComponent(
                    table,value,isSelected,hasFocus,row,column);

            int modelRow = table.convertRowIndexToModel(row);
            String status = table.getModel().getValueAt(modelRow,5).toString();

            if(status.equalsIgnoreCase("REJECTED")){
                c.setBackground(new Color(255,210,210)); // light red
            }
            else if(status.equalsIgnoreCase("PENDING")){
                c.setBackground(new Color(255,230,190)); // light orange
            }
            else if(status.equalsIgnoreCase("APPROVED")){
                c.setBackground(new Color(200,230,255)); // light blue
            }
            else{
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    });
}
    class ButtonRenderer extends JButton implements TableCellRenderer {

        String type;

        public ButtonRenderer(String type){
            this.type = type;
            setOpaque(true);
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){

            int modelRow = table.convertRowIndexToModel(row);

            String status = table.getModel().getValueAt(modelRow,5).toString();
            String paid = table.getModel().getValueAt(modelRow,6).toString();

            setText(type);

            if(type.equals("Pay")){

                if(status.equalsIgnoreCase("APPROVED") && paid.equals("NO")){
                    setBackground(new Color(0,150,0));
                }else{
                    setBackground(Color.GRAY);
                }

            }else if(type.equals("Edit")){

   if(status.equalsIgnoreCase("APPROVED") 
   || status.equalsIgnoreCase("REJECTED") 
   || status.equalsIgnoreCase("COMPLETED")){
    setBackground(Color.GRAY);
}else{
    setBackground(new Color(102,75,200));
}

}else{

    if(status.equalsIgnoreCase("APPROVED") 
   || status.equalsIgnoreCase("REJECTED") 
   || status.equalsIgnoreCase("COMPLETED")){
    setBackground(Color.GRAY);
}else{
    setBackground(new Color(200,50,50));
}
}

            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        private JButton button;
        String type;

        public ButtonEditor(String type){

            super(new JCheckBox());
            this.type = type;

            button = new JButton();
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){

            button.setText(type);

            int modelRow = table.convertRowIndexToModel(row);

            String status = table.getModel().getValueAt(modelRow,5).toString();
            String paid = table.getModel().getValueAt(modelRow,6).toString();

            if(type.equals("Pay")){

                if(status.equalsIgnoreCase("APPROVED") && paid.equals("NO")){
                    button.setBackground(new Color(0,150,0));
                    button.setEnabled(true);
                }else{
                    button.setBackground(Color.GRAY);
                    button.setEnabled(false);
                }

            }else if(type.equals("Edit")){

    if(status.equalsIgnoreCase("APPROVED") 
   || status.equalsIgnoreCase("REJECTED") 
   || status.equalsIgnoreCase("COMPLETED")){
    button.setBackground(Color.GRAY);
    button.setEnabled(false);
}else{
    button.setBackground(new Color(102,75,200));
    button.setEnabled(true);
}

}else{

   if(status.equalsIgnoreCase("APPROVED") 
   || status.equalsIgnoreCase("REJECTED") 
   || status.equalsIgnoreCase("COMPLETED")){
    button.setBackground(Color.GRAY);
    button.setEnabled(false);
}else{
    button.setBackground(new Color(200,50,50));
    button.setEnabled(true);
}
}

            return button;
        }

        public Object getCellEditorValue(){

            int modelRow = table.convertRowIndexToModel(table.getSelectedRow());

            int orderId = (int)table.getModel().getValueAt(modelRow,0);
            String status = table.getModel().getValueAt(modelRow,5).toString();
            
            if(type.equals("Pay")){
if(status.equalsIgnoreCase("APPROVED") 
   || status.equalsIgnoreCase("REJECTED") 
   || status.equalsIgnoreCase("COMPLETED")){
                    new PaymentFrame(vendorId,orderId);
                }else{
                    JOptionPane.showMessageDialog(null,"Payment already completed or not approved");
                }

            }else if(type.equals("Edit")){

                if(status.equalsIgnoreCase("APPROVED") 
   || status.equalsIgnoreCase("REJECTED") 
   || status.equalsIgnoreCase("COMPLETED")){
                    JOptionPane.showMessageDialog(null,"This order cannot be modified.");
                }else{
                    dispose();
                    new EditOrderFrame(orderId,vendorId);
                }

            }else if(type.equals("Delete")){

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Delete this order?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);

                if(confirm == JOptionPane.YES_OPTION){
                    MyOrdersFrame.this.deleteOrder(orderId);
                }
            }

            return "";
        }
    }
}