/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.MyRowRenderer;
import com.ustasoft.pos.domain.Item;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author cak-ust
 */
public class DlgLookupItem extends javax.swing.JDialog {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private Object srcForm;
    private Integer kodeBarang=null;
    private Item item;
    /**
     * Creates new form DlgLookupItem
     */
    public DlgLookupItem(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        for(int i=0; i<tblCustomer.getColumnCount(); i++){
            tblCustomer.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        for(int i=0; i<tblItem.getColumnCount(); i++){
            tblItem.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        for(int i=0; i<tblSales.getColumnCount(); i++){
            tblSales.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        
        tblItem.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tblCustomer.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tblSales.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblItem.getSelectedRow();
                if(iRow<0)
                    return;
                
                Integer idBarang=Integer.parseInt(tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("ProductID")).toString());
                String sQry="select m.id_sales, coalesce(s.nama_sales,'') as nama_sales, coalesce(m.price,0) as price "
                        + "from m_item_price_by_salesman m "
                        + "inner join m_salesman s on s.id_sales=m.id_sales "
                        + "where m.id_barang="+idBarang+" "
                        + "order by coalesce(s.nama_sales,'')";
                
                ((DefaultTableModel)tblSales.getModel()).setNumRows(0);
                try{
                    ResultSet rs=conn.createStatement().executeQuery(sQry);
                    while(rs.next()){
                        ((DefaultTableModel)tblSales.getModel()).addRow(new Object[]{
                            rs.getString("nama_sales"),
                            rs.getDouble("price"),
                            rs.getInt("id_sales"), 
                    
                        });
                    }
                    rs.close();
                    if(tblSales.getRowCount()>0){
                        tblSales.setRowSelectionInterval(0, 0);
                        tblSales.setModel(fn.autoResizeColWidth(tblSales, ((DefaultTableModel)tblSales.getModel())).getModel());
                    }
                    
                    ((DefaultTableModel)tblCustomer.getModel()).setNumRows(0);
                    sQry="select m.id_customer, coalesce(s.nama_relasi,'') as nama_customer, coalesce(m.price,0) as price "
                            + "from m_item_price_by_customer m "
                            + "inner join m_relasi s on s.id_relasi=m.id_customer "
                            + "where m.id_barang="+idBarang+" "
                            + "order by coalesce(s.nama_relasi,'')";
                    
                    rs=conn.createStatement().executeQuery(sQry);
                    while(rs.next()){
                        ((DefaultTableModel)tblCustomer.getModel()).addRow(new Object[]{
                            rs.getString("nama_customer"),
                            rs.getDouble("price"),
                            rs.getInt("id_customer"), 
                        });
                    }
                    rs.close();
                    if(tblCustomer.getRowCount()>0){
                        tblCustomer.setRowSelectionInterval(0, 0);
                        tblCustomer.setModel(fn.autoResizeColWidth(tblCustomer, ((DefaultTableModel)tblCustomer.getModel())).getModel());
                    }
                    double saldoTotal=0;
                    String sGudang="<html>";
                    String sSaldo="<html>";
                    rs=conn.createStatement().executeQuery("select * from vw_stock where id_barang="+idBarang);
                    while(rs.next()){
                        saldoTotal+=rs.getDouble("stock");
                        sGudang+="<br> "+rs.getString("nama_gudang");
                        sSaldo+="<br> <right>"+GeneralFunction.dFmt.format(rs.getDouble("stock"))+"</right>";
                    }
                    sGudang+="</html>";
                    sSaldo+="</html>";
                    lblSaldoGlobal.setText("<html>Saldo Total : <b>"+GeneralFunction.dFmt.format(saldoTotal)+"</html>");
                    lblNamaGudang.setText(sGudang);
                    lblSaldo.setText(sSaldo);
                }catch(SQLException se){
                    JOptionPane.showMessageDialog(null, se.getMessage());
                }
            }   
        });
    }
    
    public void setConn(Connection con){
        this.conn=con;
    }
    
    public void setSrcForm(Object obj){
        this.srcForm=obj;
    }
    
    private void udfFilter(){
        String sQry="select i.id, coalesce(i.plu,'') as plu, coalesce(i.nama_barang, '') as nama_barang,  "
                + "coalesce(k.nama_kategori,'') as kategori, coalesce(i.harga_jual,0) as harga_jual, "
                + "coalesce(i.satuan,'') as satuan "
                + "from m_item i "
                + "left join m_item_kategori k on k.id=i.id_kategori "
                + "where i.active=true "
                + "and coalesce(i.plu,'') || coalesce(i.nama_barang, '') ilike '%"+jTextField1.getText()+"%' "
                + "order by i.nama_barang";
        try{
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("plu"),
                    rs.getString("nama_barang"),
                    rs.getString("kategori"),
                    rs.getDouble("harga_jual"),
                    rs.getString("satuan"),
                    rs.getInt("id"), 
                    
                });
            }
            if(tblItem.getRowCount()>0){
                tblItem.setRowSelectionInterval(0, 0);
                tblItem.setModel(fn.autoResizeColWidth(tblItem, ((DefaultTableModel)tblItem.getModel())).getModel());
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void clearText() {
        jTextField1.setText("");
    }

    public Item getSelectedItem(){
        return item;
    }
    
    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
            
//            if(getTableSource()==null)
//                return;
            
            if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")) {
                fn.keyTyped(evt);

           }

        }
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                            if (!fn.isListVisible()){
                                Component c = findNextFocus();
                                if (c==null) return;
                                c.requestFocus();
                            }else{
                                fn.lstRequestFocus();
                            }
                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(evt.getSource() instanceof JTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    dispose();
                    break;
                }
                
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

        public Component findNextFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component nextFocus = policy.getComponentAfter(root, c);
                if (nextFocus == null) {
                    nextFocus = policy.getDefaultComponent(root);
                }
                return nextFocus;
            }
            return null;
        }

        public Component findPrevFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component prevFocus = policy.getComponentBefore(root, c);
                if (prevFocus == null) {
                    prevFocus = policy.getDefaultComponent(root);
                }
                return prevFocus;
            }
            return null;
        }
    }
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if( (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;
    
    public Integer getKodeBarang(){
        return kodeBarang;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnPilih = new javax.swing.JButton();
        lblSaldoGlobal = new javax.swing.JLabel();
        lblNamaGudang = new javax.swing.JLabel();
        lblSaldo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSales = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Harga Penjualan");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PLU", "Nama", "Jenis", "Harga Jual", "Satuan", "ProductID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblItem);

        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel1.setText("Pencarian Item :");

        btnPilih.setText("Pilih");
        btnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihActionPerformed(evt);
            }
        });

        lblSaldoGlobal.setText("Stok Global :");

        lblNamaGudang.setText("jLabel3");

        lblSaldo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSaldo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSaldo.setText("jLabel3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(btnPilih, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addGap(8, 8, 8))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(lblSaldoGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblNamaGudang, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPilih))
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSaldoGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNamaGudang, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Harga by Customer"));

        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Pelanggan", "Harga Terakhir", "ID Pelanggan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCustomer.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblCustomer);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Harga by Salesman"));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Salesman", "Harga Terakhir", "IDSales"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSales.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(tblSales);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 270, 130));

        jLabel2.setBackground(new java.awt.Color(0, 0, 51));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Daftar Harga");
        jLabel2.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(17, 17, 17))
        );

        setSize(new java.awt.Dimension(955, 507));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        udfFilter();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfFilter();
    }//GEN-LAST:event_formWindowOpened

    private void btnPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPilihActionPerformed
        int iRow = tblItem.getSelectedRow();
        if(iRow<0 ||!btnPilih.isVisible() ||!btnPilih.isEnabled() || srcForm==null)
            return;
        TableColumnModel col=tblItem.getColumnModel();
        Item i=new Item();
        i.setId(GeneralFunction.udfGetInt(tblItem.getValueAt(iRow, col.getColumnIndex("ProductID"))));
        i.setNamaBarang(tblItem.getValueAt(iRow, col.getColumnIndex("Nama")).toString());
        i.setPlu(tblItem.getValueAt(iRow, col.getColumnIndex("PLU")).toString());
        i.setHargaJual(GeneralFunction.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Harga Jual"))));
        item=i;
        
    }//GEN-LAST:event_btnPilihActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookupItem dialog = new DlgLookupItem(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPilih;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblNamaGudang;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JLabel lblSaldoGlobal;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTable tblItem;
    private javax.swing.JTable tblSales;
    // End of variables declaration//GEN-END:variables
}
