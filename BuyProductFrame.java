package ui;

import config.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class BuyProductFrame extends JFrame {

private JTable table;
private int vendorId;

public BuyProductFrame(int vendorId) {

    this.vendorId = vendorId;

    setTitle("Buy Products");
    setSize(1100,700);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // ===== DARK TEAL BACKGROUND =====

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

    // ===== CARD PANEL =====

    JPanel card = new JPanel(new BorderLayout());
    card.setPreferredSize(new Dimension(1000,600));
    card.setBackground(new Color(44,52,70));
    card.setBorder(new EmptyBorder(30,40,30,40));

    // ===== HEADER =====

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JLabel title = new JLabel("Buy Products");
    title.setFont(new Font("Segoe UI",Font.BOLD,26));
    title.setForeground(Color.WHITE);

    JButton backBtn = createThemeButton("Back to Dashboard");

    backBtn.addActionListener(e -> {
        new VendorDashboard(vendorId);
        dispose();
    });

    header.add(title,BorderLayout.WEST);
    header.add(backBtn,BorderLayout.EAST);

    card.add(header,BorderLayout.NORTH);

    table = new JTable();
    styleTable(table);

    loadProducts();

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getViewport().setBackground(new Color(44,52,70));

    card.add(scroll,BorderLayout.CENTER);

    background.add(card);
    add(background);

    setVisible(true);
}

private void loadProducts(){

    try{

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT product_id, category_id, product_name, unit_price, description, stock " +
                "FROM product WHERE status='ACTIVE' AND stock > 0"
        );

        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID","Category ID","Product","Price","Description","Stock","Buy"},0){

            public boolean isCellEditable(int row,int column){
                return column == 6;
            }
        };

        while(rs.next()){

            model.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getInt("category_id"),
                    rs.getString("product_name"),
                    rs.getDouble("unit_price"),
                    rs.getString("description"),
                    rs.getInt("stock"),
                    "Buy"
            });

        }

        table.setModel(model);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);

        table.getColumn("Buy").setCellRenderer(new ButtonRenderer());
        table.getColumn("Buy").setCellEditor(new ButtonEditor());

    }
    catch(Exception e){
        e.printStackTrace();
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer(){

        setOpaque(true);
        setText("Buy");
        setBackground(new Color(102,75,200));
        setForeground(Color.WHITE);
    }

    public Component getTableCellRendererComponent(JTable table,Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,int column){

        return this;
    }
}

class ButtonEditor extends DefaultCellEditor{

    private JButton button;

    public ButtonEditor(){

        super(new JCheckBox());

        button = new JButton("Buy");
        button.setBackground(new Color(102,75,200));
        button.setForeground(Color.WHITE);

        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table,Object value,
                                                 boolean isSelected,
                                                 int row,int column){

        return button;
    }

public Object getCellEditorValue(){

    int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
    DefaultTableModel model = (DefaultTableModel) table.getModel();

    int productId = (int) model.getValueAt(modelRow,0);
    int stock = (int) model.getValueAt(modelRow,5);

    if(stock <= 0){
        JOptionPane.showMessageDialog(null,"Out Of Stock");
        return "";
    }

    try{

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
        "INSERT INTO v_orders (vendor_id,product_id,units,delivery_date,status,is_deleted) VALUES (?,?,?,?, 'PENDING',0)"
        );

        ps.setInt(1,vendorId);
        ps.setInt(2,productId);
        ps.setInt(3,1);
        ps.setDate(4,new java.sql.Date(System.currentTimeMillis()));

        ps.executeUpdate();

        JOptionPane.showMessageDialog(null,
        "Order request sent to supplier.\nPayment will be available after approval.");

    }
    catch(Exception ex){
        ex.printStackTrace();
    }

    dispose();
    new BuyProductFrame(vendorId);

    return "";
}
}

private JButton createThemeButton(String text){

    JButton btn = new JButton(text);

    btn.setBackground(new Color(102,75,200));
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setBorder(BorderFactory.createEmptyBorder());
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(180,35));
    btn.setFont(new Font("Segoe UI",Font.BOLD,14));

    return btn;
}

private void styleTable(JTable table){

    table.setRowHeight(40);
    table.setFont(new Font("Segoe UI",Font.PLAIN,14));
    table.setFillsViewportHeight(true);
    table.setForeground(Color.BLACK);
    table.setBackground(Color.WHITE);

    JTableHeader header = table.getTableHeader();

    header.setFont(new Font("Segoe UI",Font.BOLD,14));
    header.setBackground(new Color(102,75,200));
    header.setForeground(Color.WHITE);
}
}