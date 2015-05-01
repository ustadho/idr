/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos;

import com.ustasoft.component.GeneralFunction;
import com.ustasoft.component.MyRowRenderer;
import com.ustasoft.pos.dao.jdbc.ItemDao;
import com.ustasoft.pos.domain.HargaItem;
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
public class DlgLookupItemBengkel extends javax.swing.JDialog {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private Object srcForm;
    private Integer kodeBarang=null;
    private Item item;
    ItemDao itemDao;
    private Integer idBarang=null;
    /**
     * Creates new form DlgLookupItem
     */
    public DlgLookupItemBengkel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        for(int i=0; i<tblStok.getColumnCount(); i++){
            tblStok.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        for(int i=0; i<tblItem.getColumnCount(); i++){
            tblItem.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        tblItem.addKeyListener(kListener);
        
        tblItem.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tblStok.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tblStok.getColumn("Nama Gudang").setPreferredWidth(200);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblItem.getSelectedRow();
                if(iRow<0)
                    return;
                
                Integer idBarang=Integer.parseInt(tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("ProductID")).toString());
                
                try{
                    ResultSet rs=null;
                    lblHargaJual.setText(fn.intFmt.format(tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("Harga Jual"))));
                    if(itemDao !=null)
                        lblLastCost.setText(fn.intFmt.format(itemDao.getPrevCost(idBarang)));
                    
                    double saldoTotal=0;
                    rs=conn.createStatement().executeQuery("select * from vw_stock where id_barang="+idBarang);
                    ((DefaultTableModel)tblStok.getModel()).setNumRows(0);
                    while(rs.next()){
                        saldoTotal+=rs.getDouble("stock");
                        ((DefaultTableModel)tblStok.getModel()).addRow(new Object[]{
                            rs.getString("nama_gudang"),
                            rs.getDouble("stock")
                        }); 
                    }
                    lblSaldo.setText(GeneralFunction.dFmt.format(saldoTotal));
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
    
    private void udfInitForm(){
        itemDao=new ItemDao(conn);
        
    }
    
    private void udfFilter(){
        String sQry="select i.id, coalesce(i.plu,'') as plu, coalesce(i.nama_barang, '') as nama_barang,  "
                + "coalesce(k.nama_kategori,'') as kategori, coalesce(i.harga_jual,0) as harga_jual, "
                + "coalesce(i.satuan,'') as satuan "
                + "from m_item i "
                + "left join m_item_kategori k on k.id=i.id_kategori "
                + "where i.active=true "
                + "and coalesce(i.plu,'') || coalesce(i.nama_barang, '') ilike '%"+txtSearch.getText()+"%' "
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
        txtSearch.setText("");
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
                            if(evt.getSource().equals(txtSearch) && tblItem.getSelectedRow()>=0){
                                udfSelected();
                                return;
                            }
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }else{
                        udfSelected();
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
    
    private void udfSelected(){
        int iRow=tblItem.getSelectedRow();
        if(iRow>=0){
            idBarang=(Integer) tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("ProductID"));
        }else{
            idBarang=null;
        }
        if(srcForm !=null)
            this.dispose();
    }
    
    public Integer getIdBarang(){
        return idBarang;
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
        txtSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnPilih = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStok = new javax.swing.JTable();
        lblSaldo = new javax.swing.JLabel();
        lblSaldoGlobal = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblLastCost = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblHargaJual = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Harga Penjualan");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 600, 360));

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 280, 20));

        jLabel1.setText("Pencarian Item :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 20));

        btnPilih.setText("Pilih");
        btnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihActionPerformed(evt);
            }
        });
        jPanel1.add(btnPilih, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 80, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 620, 450));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informasi Stok"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblStok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Gudang", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblStok.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblStok);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 270, 190));

        lblSaldo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSaldo.setText("jLabel3");
        jPanel2.add(lblSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 220, 170, 20));

        lblSaldoGlobal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSaldoGlobal.setText("Stok Global :");
        jPanel2.add(lblSaldoGlobal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 90, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 210, 290, 250));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Informasi Harga"));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Last Cost :");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 80, 20));

        lblLastCost.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblLastCost.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLastCost.setText("0");
        lblLastCost.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(lblLastCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 130, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Harga Jual :");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 20));

        lblHargaJual.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblHargaJual.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaJual.setText("0");
        lblHargaJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(lblHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 130, 20));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 50, 290, 160));

        jLabel2.setBackground(new java.awt.Color(0, 0, 51));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Informasi Stok");
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 30, 290, 20));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-955)/2, (screenSize.height-507)/2, 955, 507);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        udfFilter();
    }//GEN-LAST:event_txtSearchKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
        udfFilter();
    }//GEN-LAST:event_formWindowOpened

    private void btnPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPilihActionPerformed
        udfSelected();
        
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
            java.util.logging.Logger.getLogger(DlgLookupItemBengkel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItemBengkel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItemBengkel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgLookupItemBengkel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookupItemBengkel dialog = new DlgLookupItemBengkel(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHargaJual;
    private javax.swing.JLabel lblLastCost;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JLabel lblSaldoGlobal;
    private javax.swing.JTable tblItem;
    private javax.swing.JTable tblStok;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
